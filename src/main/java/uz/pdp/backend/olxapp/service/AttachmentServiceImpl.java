package uz.pdp.backend.olxapp.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.exception.*;
import uz.pdp.backend.olxapp.mapper.AttachmentMapper;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;
import uz.pdp.backend.olxapp.repository.AttachmentRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    @Value("${olx.app.base-folder}")
    private String baseFolderProperty;

    private static String BASE_FOLDER;

    @PostConstruct
    public void init() {
        BASE_FOLDER = baseFolderProperty;
        log.info("AttachmentService initialized. Base folder set to: {}", BASE_FOLDER);
    }
    @Override
    public AttachmentDTO getByIdAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found with id: " + id));
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public AttachmentDTO upload(MultipartFile file) {
        log.info("Uploading single file: {}", file.getOriginalFilename());
        validateImage(file);
        Attachment attachment = saveFileToStorage(file);
        Attachment saved = attachmentRepository.save(attachment);
        log.info("File successfully uploaded and saved with ID: {}", saved.getId());
        return attachmentMapper.toDto(saved);
    }

    @Override
    public List<AttachmentDTO> upload(List<MultipartFile> multipartFiles) throws AttachmentSaveException {

        log.info("Uploading {} files...", multipartFiles.size());

//        List<Attachment> attachments = multipartFiles.stream().map(this::saveFileToStorage).collect(Collectors.toList());
//
//        List<Attachment> attachments1 = attachmentRepository.saveAll(attachments);
//        return attachments1.stream().map(attachmentMapper::toDto).toList();

        List<Path> paths = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            try {
                log.debug("Validating file: {}", file.getOriginalFilename());
                validateImage(file);

                String originalFilename = file.getOriginalFilename();
                long size = file.getSize();
                String contentType = file.getContentType();
                String extension = extractExtension(originalFilename);

                Path directoryPath = buildDirectoryPath();
                Files.createDirectories(directoryPath);

                Path filePath = generateUniqueFilePath(directoryPath, extension);

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath);
                }

                Attachment attachment = new Attachment(originalFilename, contentType, size, filePath.toString());
                Attachment saved = attachmentRepository.save(attachment);

                paths.add(filePath);
                attachments.add(saved);
                log.info("File '{}' saved successfully as '{}'", originalFilename, filePath);

            } catch (IOException e) {
                log.error("Failed to save file: {}", file.getOriginalFilename(), e);
                cleanupStoredFiles(paths);
                throw new AttachmentSaveException("Error saving one of the files.");
            }
        }

        log.info("All files uploaded successfully.");
        return attachments.stream().map(attachmentMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Berilgan ro'yxatdagi barcha fayllarni jismoniy o'chiradigan yordamchi metod.
     * @param paths O'chirilishi kerak bo'lgan fayllar ro'yxati
     */
    private void cleanupStoredFiles(List<Path> paths) {
        log.warn("Rolling back saved files due to error. Deleting {} files...", paths.size());
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
                log.info("Deleted file: {}", path);
            } catch (IOException ex) {
                log.error("Failed to delete file during cleanup: {}", path, ex);
            }
        }
    }

    @Override
    public void update(Long id, MultipartFile file) {
        log.info("Updating attachment with ID: {}", id);
        validateImage(file);

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found for update: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });

        deletePhysicalFile(attachment.getPath());
        Attachment updated = saveFileToStorage(file);

        attachment.setOriginalName(updated.getOriginalName());
        attachment.setFileSize(updated.getFileSize());
        attachment.setContentType(updated.getContentType());
        attachment.setPath(updated.getPath());

        attachmentRepository.save(attachment);
        log.info("Attachment with ID {} updated successfully.", id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting attachment with ID: {}", id);
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found for deletion: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });

        deletePhysicalFile(attachment.getPath());
        attachmentRepository.delete(attachment);
        log.info("Attachment with ID {} deleted successfully.", id);
    }

    @Override
    public byte[] viewAttachment(Long id) throws IOException {
        log.debug("Viewing attachment with ID: {}", id);
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found for viewing: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });

        log.info("Returning byte content of attachment ID: {}", id);
        return Files.readAllBytes(Path.of(attachment.getPath()));
    }

    private Attachment saveFileToStorage(MultipartFile file) throws AttachmentSaveException {
        try {
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            String extension = extractExtension(originalFilename);

            Path directoryPath = buildDirectoryPath();
            Files.createDirectories(directoryPath);

            Path filePath = generateUniqueFilePath(directoryPath, extension);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath);
            }

            log.debug("Saved file '{}' to '{}'", originalFilename, filePath);
            return new Attachment(originalFilename, contentType, size, filePath.toString());

        } catch (IOException e) {
            log.error("Error saving file: {}", file.getOriginalFilename(), e);
            throw new AttachmentSaveException("Error saving file: " + file.getOriginalFilename());
        }
    }

    private void deletePhysicalFile(String pathStr) {
        Path path = Path.of(pathStr);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Physical file deleted: {}", pathStr);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", pathStr, e);
            throw new FileDeletionException("Error deleting file: " + pathStr);
        }
    }

    private String extractExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            String extension = filename.substring(filename.lastIndexOf("."));
            log.debug("Extracted extension '{}' from filename '{}'", extension, filename);
            return extension;
        }
        log.debug("No extension found for filename '{}'", filename);
        return "";
    }

    private Path buildDirectoryPath() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = now.getMonth().name().charAt(0) + now.getMonth().name().substring(1).toLowerCase();
        Path path = Path.of(BASE_FOLDER, year, month);
        log.debug("Generated directory path: {}", path);
        return path;
    }

    private Path generateUniqueFilePath(Path directoryPath, String extension) throws IOException {
        Path filePath;
        String uniqueName;
        int attempt = 0;
        do {
            uniqueName = UUID.randomUUID() + extension;
            filePath = directoryPath.resolve(uniqueName);
            attempt++;
        } while (Files.exists(filePath));

        log.debug("Generated unique file path '{}' on attempt {}", filePath, attempt);
        return filePath;
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("Invalid file type: {}", contentType);
            throw new InvalidImageFileException("Only image files are allowed.");
        }

        if (filename == null ||
                !(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
            log.warn("Unsupported file extension: {}", filename);
            throw new InvalidImageFileException("Allowed formats: .png, .jpg, .jpeg");
        }
    }
}
