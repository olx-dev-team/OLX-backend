package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FavoriteReqDTO {

    @Positive(message = "Product Id must be positive")
    @NotNull(message = "Product Id can't be null")
    private Long productId;

}
