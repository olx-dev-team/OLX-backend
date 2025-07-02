package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.enums.RejectionReasonEnum;

import java.util.Set;

/**
 * Created by Avazbek on 02/07/25 21:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModerationListDTO {
    private Long id;

    private String title;

    private String mainImageUrl;

    private Set<RejectionReasonEnum> rejectionReasons;


}
