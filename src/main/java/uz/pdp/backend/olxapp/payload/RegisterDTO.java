package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required and should be unique")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 2, message = "Password must have at least {min} characters")
    private String password;

    @Email(message = "Invalid email address")
    private String email;

    private String phoneNumber;


}
