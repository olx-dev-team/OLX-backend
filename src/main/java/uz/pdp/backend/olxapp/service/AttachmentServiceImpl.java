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
        log.info("Attachment base folder initialized as: {}", BASE_FOLDER);
    }

    @Override
    public AttachmentDTO getByIdAttachment(Long id) {
        log.info("Fetching attachment with ID: {}", id);
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found with ID: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public AttachmentDTO upload(MultipartFile file) {
        log.info("Uploading single file: {}", file.getOriginalFilename());
        validateImage(file);
        Attachment attachment = saveFileToStorage(file);
        Attachment saved = attachmentRepository.save(attachment);
        log.info("File saved with ID: {}", saved.getId());
        return attachmentMapper.toDto(saved);
    }

    @Override
    public List<AttachmentDTO> upload(List<MultipartFile> multipartFiles) {
        log.info("Uploading {} files in bulk", multipartFiles.size());
        List<Path> paths = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            try {
                log.debug("Processing file: {}", file.getOriginalFilename());
                Attachment attachment = saveFileToStorage(file);
                Attachment saved = attachmentRepository.save(attachment);
                paths.add(Path.of(saved.getPath()));
                attachments.add(saved);
                log.info("Saved file: {} with ID: {}", saved.getOriginalName(), saved.getId());
            } catch (AttachmentSaveException e) {
                log.error("Failed to save file: {}", file.getOriginalFilename(), e);
                cleanupStoredFiles(paths);
                throw new AttachmentSaveException("Failed to save all files. Transaction rolled back.");
            }
        }

        return attachments.stream().map(attachmentMapper::toDto).collect(Collectors.toList());
    }


    /**
     * Berilgan ro'yxatdagi barcha fayllarni jismoniy o'chiradigan yordamchi metod.
     * @param paths O'chirilishi kerak bo'lgan fayllar ro'yxati
     */
    private void cleanupStoredFiles(List<Path> paths) {
        log.warn("Cleanup initiated for {} files", paths.size());
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
                log.warn("Deleted file: {}", path);
            } catch (IOException ex) {
                log.error("Could not delete file during rollback: {}", path, ex);
            }
        }
    }

    @Override
    public void update(Long id, MultipartFile file) {
        log.info("Updating file with ID: {}", id);
        validateImage(file);

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found with ID: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });

        deletePhysicalFile(attachment.getPath());
        Attachment updated = saveFileToStorage(file);

        attachment.setOriginalName(updated.getOriginalName());
        attachment.setFileSize(updated.getFileSize());
        attachment.setContentType(updated.getContentType());
        attachment.setPath(updated.getPath());

        attachmentRepository.save(attachment);
        log.info("Attachment with ID {} successfully updated", id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Deleting attachment with ID: {}", id);
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attachment not found with ID: {}", id);
                    return new FileNotFountException("Attachment not found with id: " + id);
                });

        deletePhysicalFile(attachment.getPath());
        attachmentRepository.delete(attachment);
        log.info("Attachment with ID {} deleted", id);
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

            return new Attachment(
                    originalFilename,
                    contentType,
                    size,
                    filePath.toString()
            );

        } catch (IOException e) {
            throw new AttachmentSaveException("Error saving file: " + file.getOriginalFilename());
        }
    }

    private void deletePhysicalFile(String pathStr) {
        Path path = Path.of(pathStr);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Deleted physical file: {}", pathStr);
            }
        } catch (IOException e) {
            log.error("Error deleting file: {}", pathStr, e);
            throw new FileDeletionException("Error deleting file: " + pathStr);
        }
    }

    private String extractExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    private Path buildDirectoryPath() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = now.getMonth().name().charAt(0) + now.getMonth().name().substring(1).toLowerCase();
        return Path.of(BASE_FOLDER, year, month);
    }

    private Path generateUniqueFilePath(Path directoryPath, String extension) throws IOException {
        Path filePath;
        String uniqueName;
        do {
            uniqueName = UUID.randomUUID() + extension;
            filePath = directoryPath.resolve(uniqueName);
        } while (Files.exists(filePath));
        return filePath;
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (filename != null && (filename.contains("..") || filename.contains("/") || filename.contains("\\"))) {
            throw new InvalidImageFileException("Invalid file name");
        }

        log.debug("Validating file: {}", filename);

        if (contentType == null || !contentType.startsWith("image/")) {
            log.warn("Invalid content type: {}", contentType);
            throw new InvalidImageFileException("Only image files are allowed.");
        }

        if (filename == null ||
                !(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
            log.warn("Invalid file extension: {}", filename);
            throw new InvalidImageFileException("Allowed formats: .png, .jpg, .jpeg");
        }

        log.debug("File validated: {}", filename);
    }
}
