package uz.pdp.backend.olxapp.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.*;
import uz.pdp.backend.olxapp.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link Product}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 5000, message = "Description can not exceed {max} characters")
    private String description;

    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal price;

    /* private Boolean isApproved = false; */ private Status status;

    private Integer viewCounter = 0;

    private Long categoryId;

//    private List<FavoritesDTO> favorites = new ArrayList<>();

    private List<ProductImageDTO> productImages = new ArrayList<>();

    private Long createdById;

}