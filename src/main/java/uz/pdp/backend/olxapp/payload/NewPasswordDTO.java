package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewPasswordDTO {

    private String token;

    @NotBlank
    @Size(min = 6,message = "password must be at least 6 characters")
    private String newPassword;

    @NotBlank
    @Size(min = 6,message = "password must be at least 6 characters")
    private String confirmPassword;
}
