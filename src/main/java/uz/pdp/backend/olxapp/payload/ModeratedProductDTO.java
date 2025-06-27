package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.ProductImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Avazbek on 26/06/25 13:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModeratedProductDTO {

    private Long id;

    private String title;

    private String description;

    private Boolean isApproved = false;
    

}
