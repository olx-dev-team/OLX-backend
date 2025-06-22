// ProductUpdateDTO
package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private Long categoryId;

    private boolean active;

    // 1. Qoldirilishi kerak bo'lgan eski rasmlarning ID'lari.
    // Front-end o'chirilmagan barcha eski rasmlarning ID'larini shu yerga yuboradi.
    private List<Long> keptImageIds;

    // 2. Asosiy rasm identifikatori (BITTA MAYDON).
    // Agar eski rasm asosiy bo'lsa -> uning ID'si (masalan, "123").
    // Agar yangi rasm asosiy bo'lsa -> uning fayl nomi (masalan, "yangi-rasm.jpg").
    private String mainImageIdentifier;
}