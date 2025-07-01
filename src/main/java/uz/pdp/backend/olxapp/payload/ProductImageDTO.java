package uz.pdp.backend.olxapp.payload;

import lombok.*;
import uz.pdp.backend.olxapp.entity.ProductImage;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ProductImage}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO implements Serializable {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean active;
    Long id;
    Long productId;
    Long attachmentId;
    boolean isMain;
}