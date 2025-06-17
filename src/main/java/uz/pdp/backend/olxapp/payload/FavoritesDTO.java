package uz.pdp.backend.olxapp.payload;

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

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    private Long userId;

    private Long productId;
}