package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<AttachmentDTO> getAttachment(@PathVariable Long id) {

        return ResponseEntity.ok().body(attachmentService.getByIdAttachment(id));

    }


    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachment")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok().body(attachmentService.upload(multipartFile));
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachments")
    public ResponseEntity<?> uploadFile(@RequestParam("files") List<MultipartFile> multipartFiles) {
        List<AttachmentDTO> attachmentDTOS = attachmentService.upload(multipartFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentDTOS);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PutMapping("/close/v1/attachment/{id}")
    public ResponseEntity<?> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile multipartFile) {
        attachmentService.update(id, multipartFile);
        return ResponseEntity.ok().build();
    }
}

