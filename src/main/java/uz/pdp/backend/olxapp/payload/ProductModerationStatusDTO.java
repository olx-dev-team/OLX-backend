package uz.pdp.backend.olxapp.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.enums.RejectionReasonEnum;
import uz.pdp.backend.olxapp.enums.Status;

import java.util.Set;

/**
 * Created by Avazbek on 02/07/25 20:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductModerationStatusDTO {

    private Long productId;

    private Status status;

    private Set<RejectionReasonEnum> rejectionReasons;


}
