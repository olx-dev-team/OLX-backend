package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;
import uz.pdp.backend.olxapp.service.AttachmentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttachmentController {

    private final AttachmentService attachmentService;


    @GetMapping("/open/v1/attachment/{id}")
    public ResponseEntity<AttachmentDTO> getAttachments(@PathVariable Long id) {

        return ResponseEntity.ok().body(attachmentService.getByIdAttachment(id));

    }


    @PostMapping("/close/v1/attachment")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok().body(attachmentService.upload(multipartFile));
    }

    @PostMapping("/close/v1/attachments")
    public ResponseEntity<?> uploadFile(@RequestParam("file") List<MultipartFile> multipartFiles) {
        attachmentService.upload(multipartFiles);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/close/v1/attachment/update/{id}")
    public ResponseEntity<?> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile multipartFile) {
        attachmentService.update(id, multipartFile);
        return ResponseEntity.ok().build();
    }
}

