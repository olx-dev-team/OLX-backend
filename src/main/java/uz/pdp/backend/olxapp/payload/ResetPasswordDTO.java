package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResetPasswordDTO {

    @Email(message = "email must be valid")
    @NotBlank(message = "email can not be blank")
    private String email;

}
