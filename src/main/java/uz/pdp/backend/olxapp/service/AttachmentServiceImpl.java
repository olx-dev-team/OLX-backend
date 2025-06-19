package uz.pdp.backend.olxapp.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.exception.AttachmentSaveException;
import uz.pdp.backend.olxapp.exception.FileDeletionException;
import uz.pdp.backend.olxapp.exception.FileNotFountException;
import uz.pdp.backend.olxapp.exception.InvalidImageFileException;
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

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Value("${olx.app.base-folder}")
    private String baseFolderProperty; // non-static

    private static String BASE_FOLDER;

    @PostConstruct
    public void init() {
        BASE_FOLDER = baseFolderProperty;
    }

    @Override
    public AttachmentDTO getByIdAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Not found"));
//        return new AttachmentDTO(); // TODO: Map to actual DTO
        return new AttachmentDTO(
                attachment.getCreatedAt(),
                attachment.getUpdatedAt(),
                attachment.isActive(),
                attachment.getId(),
                attachment.getOriginalName(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getPath()
        );
    }

    @Override
    public AttachmentDTO upload(MultipartFile file) {
        try {
            validateImage(file); // ✅ Fayl turi tekshiruvi

            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();

            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            LocalDate now = LocalDate.now();
            String year = String.valueOf(now.getYear());
            String month = now.getMonth().name();
            month = month.charAt(0) + month.substring(1).toLowerCase();
            Path directoryPath = Path.of(BASE_FOLDER, year, month);
            Files.createDirectories(directoryPath);

            Path filePath;
            String uniqueName;
            do {
                uniqueName = UUID.randomUUID() + extension;
                filePath = directoryPath.resolve(uniqueName);
            } while (Files.exists(filePath));

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
            return new AttachmentDTO(
                    saved.getCreatedAt(),
                    saved.getUpdatedAt(),
                    saved.isActive(),
                    saved.getId(),
                    saved.getOriginalName(),
                    saved.getContentType(),
                    saved.getFileSize(),
                    saved.getPath()
            );

        } catch (IOException e) {
            throw new AttachmentSaveException("Error saving attachment: " + e.getMessage());
        }
    }

    @Override
    public void upload(List<MultipartFile> multipartFiles) {
        List<AttachmentDTO> uploaded = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            validateImage(file); // ✅ Ruxsat etilgan fayl turi

            try {
                String originalFilename = file.getOriginalFilename();
                long size = file.getSize();
                String contentType = file.getContentType();

                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                LocalDate now = LocalDate.now();
                String year = String.valueOf(now.getYear());
                String month = now.getMonth().name();
                month = month.charAt(0) + month.substring(1).toLowerCase();

                Path directoryPath = Path.of(BASE_FOLDER, year, month);
                Files.createDirectories(directoryPath);

                Path filePath;
                String uniqueName;
                do {
                    uniqueName = UUID.randomUUID() + extension;
                    filePath = directoryPath.resolve(uniqueName);
                } while (Files.exists(filePath));

                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath);
                }

                Attachment attachment = new Attachment(
                        originalFilename,
                        contentType,
                        size,
                        filePath.toString()
                );

                attachmentRepository.save(attachment);

            } catch (IOException e) {
                throw new AttachmentSaveException("Faylni saqlashda xatolik: " + file.getOriginalFilename());
            }
        }

    }


    @Override
    public void update(Long id, MultipartFile file) {
        validateImage(file); // ✅ Fayl turi tekshiruvi

        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found"));

        // 1. Eski faylni diskdan o‘chirish
        Path oldPath = Path.of(attachment.getPath());
        try {
            if (Files.exists(oldPath)) {
                Files.delete(oldPath);
            }
        } catch (IOException e) {
            throw new FileDeletionException("File deletion error: " + e.getMessage());
        }

        try {
            // 2. Fayl ma’lumotlari
            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            LocalDate now = LocalDate.now();
            String year = String.valueOf(now.getYear());
            String month = now.getMonth().name();
            month = month.charAt(0) + month.substring(1).toLowerCase();
            Path directoryPath = Path.of(BASE_FOLDER, year, month);
            Files.createDirectories(directoryPath);

            String uniqueName;
            Path newPath;
            do {
                uniqueName = UUID.randomUUID() + extension;
                newPath = directoryPath.resolve(uniqueName);
            } while (Files.exists(newPath));

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, newPath);
            }

            // 3. DB yangilanishi
            attachment.setOriginalName(originalFilename);
            attachment.setFileSize(size);
            attachment.setContentType(contentType);
            attachment.setPath(newPath.toString());

            attachmentRepository.save(attachment);

        } catch (IOException e) {
            throw new RuntimeException("File save error: " + e.getMessage());
        }
    }


    @Override
    public void deleteById(Long id) {

        Attachment attachment;
        attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new FileNotFountException("Attachment not found"));

        Path filePath = Path.of(attachment.getPath());

        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new FileDeletionException("File deletion error: " + e.getMessage());
        }

        attachmentRepository.delete(attachment);
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidImageFileException("Faqat rasm fayllarga ruxsat beriladi.");
        }

        if (filename == null ||
                !(filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg"))) {
            throw new InvalidImageFileException("Faqat .png, .jpg va .jpeg fayllarga ruxsat beriladi.");
        }
    }


}
