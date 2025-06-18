package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.mapper.AttachmentMapper;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;

/**
 * Created by Avazbek on 18/06/25 17:22
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final AttachmentMapper attachmentMapper;


    @GetMapping
    public AttachmentDTO getAttachment() {
        Attachment attachment = new Attachment();

        attachment.setContentType("text/plain");
        attachment.setOriginalName("test.txt");
        attachment.setFileSize(34L);
        attachment.setPath("C:\\Users\\Avazbek\\Desktop\\test.txt");
        return attachmentMapper.toDto(attachment);

    }
}
