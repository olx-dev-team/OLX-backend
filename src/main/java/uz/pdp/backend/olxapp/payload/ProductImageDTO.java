package uz.pdp.backend.olxapp.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;

    boolean active;

    Long id;

    Long productId;

    Long attachmentId;

    boolean isMain;

}