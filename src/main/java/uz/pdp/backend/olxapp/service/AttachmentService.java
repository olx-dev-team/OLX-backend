package uz.pdp.backend.olxapp.service;

import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.exception.AttachmentSaveException;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;

import java.util.List;

public interface AttachmentService {
    AttachmentDTO getByIdAttachment(Long id);

    AttachmentDTO upload(MultipartFile file);

    List<AttachmentDTO> upload(List<MultipartFile> multipartFiles) throws AttachmentSaveException;


    void update(Long id, MultipartFile file);

    void deleteById(Long id);
}
