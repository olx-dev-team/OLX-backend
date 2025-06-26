package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryReqDTO {

    @NotBlank(message = "name bo'sh bolmasligi kerak")
    private String name;

    private Long parentId;

    private Boolean active;

}
