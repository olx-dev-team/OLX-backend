package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Attachment Controller", description = "APIs for uploading, retrieving and updating attachments (files, images)")
public class AttachmentController {

    private final AttachmentService attachmentService;


    /**
     * Test successfully
     * @param id - attachment id
     * @return {@link AttachmentDTO}
     */
    @Operation(
            summary = "Get attachment metadata by ID",
            description = "Returns metadata (filename, size, type) of an attachment"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment metadata retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AttachmentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @GetMapping("/open/v1/attachments/{id}")
    public ResponseEntity<AttachmentDTO> getAttachment(
            @Parameter(description = "ID of the attachment to retrieve", example = "5")
            @PathVariable Long id) {
        return ResponseEntity.ok().body(attachmentService.getByIdAttachment(id));
    }


    /**
     * Test successfully
     * @param id - attachment id
     * @return {@link AttachmentDTO}
     * @throws Exception - if attachment is not found
     */
    @Operation(
            summary = "Get raw file content (image, etc.) by ID",
            description = "Returns the byte[] content of the file (can be used to preview/download)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment retrieved successfully as byte stream"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @GetMapping("/open/v1/attachment/{id}")
    public ResponseEntity<byte[]> viewAttachment(
            @Parameter(description = "ID of the file to retrieve", example = "5")
            @PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(attachmentService.viewAttachment(id));
    }

    @Operation(
            summary = "Upload a single file",
            description = "Uploads a file and returns its metadata. Only accessible by ADMIN or USER roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(schema = @Schema(implementation = AttachmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size")
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachment")
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Single file to upload (image/png, image/jpg, etc.)")
            @RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok().body(attachmentService.upload(multipartFile));
    }

    @Operation(
            summary = "Upload multiple files",
            description = "Uploads multiple files (up to 8) and returns list of metadata. Only for ADMIN or USER roles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Files uploaded successfully",
                    content = @Content(schema = @Schema(implementation = AttachmentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or too many files")
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/close/v1/attachments")
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "List of files to upload (images only)", required = true)
            @RequestParam("files") List<MultipartFile> multipartFiles) {
        List<AttachmentDTO> attachmentDTOS = attachmentService.upload(multipartFiles);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachmentDTOS);
    }

    @Operation(
            summary = "Update an existing file",
            description = "Replaces an existing file with a new one by its ID. Only ADMIN or USER can perform this action."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File updated successfully"),
            @ApiResponse(responseCode = "404", description = "Attachment not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/close/v1/attachment/{id}")
    public ResponseEntity<?> updateFile(
            @Parameter(description = "ID of the file to update", example = "7")
            @PathVariable Long id,

            @Parameter(description = "New file to replace the existing one")
            @RequestParam("file") MultipartFile multipartFile) {

        attachmentService.update(id, multipartFile);
        return ResponseEntity.ok().build();
    }
}

