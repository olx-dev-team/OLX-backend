package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Attachment}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO implements Serializable {

    private boolean active;

    private Long id;

    private String originalName;

    private String contentType;

    private Long fileSize;

    private String path;

    private Boolean isMain;
}
