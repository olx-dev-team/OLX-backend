package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDTO {

    @NotBlank(message = "username bo'sh bolmasligi kerak")
    private String username;

    @NotBlank(message = "password bo'sh bolmasligi kerak")
    private String password;
}
