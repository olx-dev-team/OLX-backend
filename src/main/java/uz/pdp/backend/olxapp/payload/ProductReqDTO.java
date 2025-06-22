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
public class ProductReqDTO {


    @NotBlank(message = "Title is required")
    private String title;

    @Size(max = 5000, message = "Description can not exceed {max} characters")
    private String description;

    @PositiveOrZero(message = "Price must be positive or zero")
    private BigDecimal price;

    private Boolean isApproved = false;

    private Long categoryId;

    private boolean active;

    // 2. Asosiy rasm identifikatori (BITTA MAYDON).
    // Agar yangi rasm asosiy bo'lsa -> uning fayl nomi (masalan, "yangi-rasm.jpg").
    private String mainImageIdentifier;

}
