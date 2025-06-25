// ProductUpdateDTO
package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductUpdateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 5000, message = "Description can not exceed {max} characters")
    private String description;

    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal price;

    private boolean active;

    private List<ExistedImageDTO> existedImages = new ArrayList<>();

    private List<ProductNewImageDTO> productNewImages = new ArrayList<>();


}