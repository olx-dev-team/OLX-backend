package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Avazbek on 23/06/25 22:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMessageDTO {


    @NotBlank(message = "Message content cannot be empty")
    private String content;

}
