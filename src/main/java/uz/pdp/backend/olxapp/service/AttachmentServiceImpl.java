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
    }

    @Override
    public AttachmentDTO getByIdAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found with id: " + id));
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public AttachmentDTO upload(MultipartFile file) {
        validateImage(file);
        Attachment attachment = saveFileToStorage(file);
        Attachment saved = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(saved);
    }

    @Override
    public List<AttachmentDTO> upload(List<MultipartFile> multipartFiles) throws AttachmentSaveException {

//        List<Attachment> attachments = multipartFiles.stream().map(this::saveFileToStorage).collect(Collectors.toList());
//
//        List<Attachment> attachments1 = attachmentRepository.saveAll(attachments);
//        return attachments1.stream().map(attachmentMapper::toDto).toList();

        List<Path> paths = new ArrayList<>();
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {

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

                Attachment attachment = new Attachment(
                        originalFilename,
                        contentType,
                        size,
                        filePath.toString()
                );

                Attachment saved = attachmentRepository.save(attachment);
                paths.add(filePath);
                attachments.add(saved);


            } catch (IOException e) {

                log.warn(e.getMessage());
                cleanupStoredFiles(paths);

            }


        }

        return attachments.stream().map(attachmentMapper::toDto).collect(Collectors.toList());


    }

    /**
     * Berilgan ro'yxatdagi barcha fayllarni jismoniy o'chiradigan yordamchi metod.
     * @param paths O'chirilishi kerak bo'lgan fayllar ro'yxati
     */
    private void cleanupStoredFiles(List<Path> paths) {
        for (Path path : paths) {
            try {
                Files.deleteIfExists(path);
                // Bu yerda log yozish mumkin, masalan: log.warn("Rollback due to error. Deleting file: {}", path);
            } catch (IOException ex) {
                // Faylni o'chirishda xatolik bo'lsa, uni log'ga yozib qo'yamiz,
                // lekin asosiy xatolikni "yutib yubormaslik" uchun yangi exception tashlamaymiz.
                 log.error("Could not delete file {} during cleanup", path, ex);
            }
        }
    }

    @Override
    public void update(Long id, MultipartFile file) {
        validateImage(file);

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found with id: " + id));

        deletePhysicalFile(attachment.getPath());
        Attachment updated = saveFileToStorage(file);

        attachment.setOriginalName(updated.getOriginalName());
        attachment.setFileSize(updated.getFileSize());
        attachment.setContentType(updated.getContentType());
        attachment.setPath(updated.getPath());

        attachmentRepository.save(attachment);
    }

    @Override
    public void deleteById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found with id: " + id));

        deletePhysicalFile(attachment.getPath());
        attachmentRepository.delete(attachment);
    }

    @Override
    public byte[] viewAttachment(Long id) throws IOException {

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found with id: " + id));

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
            }
        } catch (IOException e) {
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

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidImageFileException("Only image files are allowed.");
        }

        if (filename == null ||
                !(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
            throw new InvalidImageFileException("Allowed formats: .png, .jpg, .jpeg");
        }
    }
}
