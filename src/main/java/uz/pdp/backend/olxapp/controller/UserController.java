package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Endpoints for managing users, roles, and password operations")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "Get all users",
            description = "Retrieves all users with pagination. Access limited to admins.")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/close/v1/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        PageDTO<UserDTO> userDTOPage = userService.getAll(page, size);
        return ResponseEntity.ok(userDTOPage);
    }

    @Operation(summary = "Get all inactive users",
            description = "Returns paginated list of users with inactive status. Access limited to admins.")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/close/v1/users/inactive")
    public PageDTO<?> getAllInactiveUsers(@RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAllInactive(page, size);
    }

    @Operation(summary = "Change user role", description = "Changes the role of a user by ID. Public endpoint.")
    @PutMapping("/close/v1/users/changed/{id}")
    public void changeUserRole(@PathVariable Long id, @RequestBody ChangeUserRole changeUserRole) {
        userService.changeUserRole(id, changeUserRole);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a user by ID. Accessible by USER and ADMIN.")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/close/v1/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Update user profile", description = "Allows a user to update their profile. Returns new token if username is changed.")
    @PreAuthorize("hasAnyRole('USER','ADMIN','MODERATOR')")
    @PutMapping("/close/v1/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUser updateUser) {
        UserDTO updatedUser = userService.updateUser(updateUser, id);
        if (updatedUser.getUsername().equals(updateUser.getUsername())) {
            return ResponseEntity.accepted().build();
        }
        String token = jwtService.generateToken(updatedUser.getUsername());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new TokenDTO(token));
    }

    @Operation(summary = "Update user password", description = "Allows a user to change their password by providing old and new password.")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/close/v1/users/password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPassword updateUserPassword) {
        UserDTO updatedUser = userService.updatePassword(updateUserPassword, id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);
    }

    @Operation(summary = "Request password reset", description = "Sends a reset link to the user via email or other means.")
    @PutMapping("/open/v1/users/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        ApiResponse apiResponse = userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Validate reset token", description = "Validates the reset token sent to the user.")
    @PostMapping("/open/v1/users/reset-password/token")
    public ResponseEntity<?> resetPasswordByToken(@RequestBody @Valid TokenDTO resetPasswordByTokenDTO) {
        TokenDTO tokenDTO = userService.resetPasswordByToken(resetPasswordByTokenDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    @Operation(summary = "Set new password", description = "Allows a user to set a new password using a valid token.")
    @PostMapping("/open/v1/users/reset-password/new-password")
    public ResponseEntity<?> changeNewPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        userService.changeNewPassword(newPassword);
        return ResponseEntity.ok("Successfully changed your password");
    }
}
