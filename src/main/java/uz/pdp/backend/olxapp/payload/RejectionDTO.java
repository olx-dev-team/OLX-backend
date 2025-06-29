package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.enums.RejectionReasonEnum;

import java.util.List;

/**
 * Created by Avazbek on 29/06/25 16:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDTO {

    @NotEmpty(message = "Please select at least one rejection reason")
    private List<RejectionReasonEnum> reasons;
}
