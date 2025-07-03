package uz.pdp.backend.olxapp.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for {@link uz.pdp.backend.olxapp.entity.Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    @NotBlank(message = "Name is required and unique")
    private String name;

    private Long parentId;

    private List<CategoryDTO> children = new ArrayList<>();
//
//    private List<ProductDTO> products = new ArrayList<>();

}
