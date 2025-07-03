package uz.pdp.backend.olxapp.payload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordDTO {

    @Schema(description = "user email", example = "example@gmail.com")
    @Email(message = "email must be valid")
    @NotBlank(message = "email can not be blank")
    private String email;

}
