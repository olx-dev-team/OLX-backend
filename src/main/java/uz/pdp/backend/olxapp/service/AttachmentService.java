package uz.pdp.backend.olxapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;

import java.util.List;

public interface AttachmentService {
    AttachmentDTO getByIdAttachment(Long id);

    AttachmentDTO upload(MultipartFile file);

    void upload(List<MultipartFile> multipartFiles);

    void update(Long id, MultipartFile file);

    void deleteById(Long id);
}
