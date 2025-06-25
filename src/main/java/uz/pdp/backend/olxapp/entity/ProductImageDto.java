package uz.pdp.backend.olxapp.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ProductImage}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDto implements Serializable {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean active;
    Long id;
    Long productId;
    Long attachmentId;
    boolean isMain;
}