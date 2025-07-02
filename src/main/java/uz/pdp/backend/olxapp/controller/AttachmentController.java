package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Attachment", description = "Operations related to file attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(
            summary = "Get Attachment by ID",
            description = "Returns the metadata of an attachment by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment found"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found")
            }
    )
    @GetMapping("/open/v1/attachment/{id}")
    public ResponseEntity<AttachmentDTO> getAttachment(@PathVariable Long id) {
        return ResponseEntity.ok().body(attachmentService.getByIdAttachment(id));
    }

    @Operation(
            summary = "Upload single file",
            description = "Uploads one file and returns its metadata",
            requestBody = @RequestBody(
                    required = true,
                    description = "File to upload",
                    content = @Content(mediaType = "multipart/form-data")
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file or request")
            }
    )
    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachment")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file")
            @Schema(description = "File to upload", type = "string", format = "binary")
            MultipartFile multipartFile
    ) {
        return ResponseEntity.ok().body(attachmentService.upload(multipartFile));
    }

    @Operation(
            summary = "Upload multiple files",
            description = "Uploads a list of files and returns their metadata",
            requestBody = @RequestBody(
                    required = true,
                    description = "Files to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "array", format = "binary")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Files uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid files or request")
            }
    )
    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachments")
    public ResponseEntity<?> uploadFile(
            @RequestParam("files")
            @Schema(description = "List of files to upload", type = "array", format = "binary")
            List<MultipartFile> multipartFiles
    ) {
        List<AttachmentDTO> attachmentDTOS = attachmentService.upload(multipartFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentDTOS);
    }

    @Operation(
            summary = "Update file",
            description = "Replaces an existing file with a new one",
            requestBody = @RequestBody(
                    required = true,
                    description = "New file to upload",
                    content = @Content(mediaType = "multipart/form-data")
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "File updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid file")
            }
    )
    @PreAuthorize(value = "hasAnyRole('ADMIN','USER')")
    @PutMapping("/close/v1/attachment/{id}")
    public ResponseEntity<?> updateFile(
            @PathVariable Long id,
            @RequestParam("file")
            @Schema(description = "New file to replace the old one", type = "string", format = "binary")
            MultipartFile multipartFile
    ) {
        attachmentService.update(id, multipartFile);
        return ResponseEntity.ok().build();
    }
}