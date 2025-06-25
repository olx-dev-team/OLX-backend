package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    AttachmentDTO toDto(Attachment attachment);

    Attachment toEntity(AttachmentDTO attachmentDTO);

    List<Attachment> toEntity(List<AttachmentDTO> upload);
}
