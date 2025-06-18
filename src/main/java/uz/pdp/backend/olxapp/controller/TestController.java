package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.mapper.AttachmentMapper;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;

import java.time.LocalDateTime;

/**
 * Created by Avazbek on 18/06/25 15:07
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final AttachmentMapper attachmentMapper;

    @GetMapping()
    public AttachmentDTO testMapper() {
        Attachment attachment = new Attachment();

        attachment.setOriginalName("test");
        attachment.setContentType("image/png");
        attachment.setFileSize(34L);
        attachment.setPath("path");
        attachment.setActive(true);
        attachment.setCreatedAt(LocalDateTime.now());
        attachment.setUpdatedAt(LocalDateTime.now());


        return attachmentMapper.toDto(attachment);







    }

}
