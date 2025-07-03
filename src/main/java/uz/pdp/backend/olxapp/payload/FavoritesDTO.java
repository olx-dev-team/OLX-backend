package uz.pdp.backend.olxapp.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.Favorites;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for {@link Favorites}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoritesDTO implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    private Long userId;

    private Long productId;
}