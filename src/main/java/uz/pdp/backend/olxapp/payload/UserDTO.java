package uz.pdp.backend.olxapp.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.pdp.backend.olxapp.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * DTO for {@link User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required and should be unique")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have at least {min} characters")
    private String password;

    @Email(message = "Invalid email address")
    private String email;

    private String phoneNumber;

    private Long roleId;

    private List<FavoritesDTO> favorites;

    private List<NotificationDTO>
            receivedNotifications;

    private List<NotificationDTO> sentNotifications;

    private List<ProductDTO> products;

    private List<ChatDTO> sentChats;

    private List<ChatDTO> receivedChats;

}