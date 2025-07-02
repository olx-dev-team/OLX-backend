package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Operations related to user management")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Operation(
            summary = "Get all users (ADMIN only)",
            description = "Returns a paginated list of users",
            parameters = {
                    @Parameter(name = "page", description = "Page number", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "10")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users returned successfully")
            }
    )
    @PreAuthorize(value = "hasAnyRole('ADMIN')")
    @GetMapping("/close/v1/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {

        PageDTO<UserDTO> userDTOPage = userService.getAll(page, size);
        return ResponseEntity.ok(userDTOPage);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns the user details by their ID",
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, example = "1")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize(value = "hasAnyRole('USER','ADMIN')")
    @GetMapping("/close/v1/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Update user profile",
            description = "Allows a user to update their profile information. If the username was changed, a new JWT token is returned.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateUser.class),
                            examples = @ExampleObject(
                                    value = "{ \"username\": \"newuser\", \"email\": \"new@example.com\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "User updated successfully or new token issued")
            }
    )
    @PreAuthorize(value = "hasRole('USER')")
    @PutMapping("/close/v1/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUser updateUser) {

        UserDTO updatedUser = userService.updateUser(updateUser, id);

        if (updatedUser.getUsername().equals(updateUser.getUsername())) {
            return ResponseEntity.accepted().build();
        }
        String token = jwtService.generateToken(updatedUser.getUsername());
        TokenDTO newToken = new TokenDTO(token);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newToken);
    }

    @Operation(
            summary = "Update user password",
            description = "Allows a user to change their password",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateUserPassword.class),
                            examples = @ExampleObject(
                                    value = "{ \"oldPassword\": \"old123\", \"newPassword\": \"newSecure456\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Password updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Old password is incorrect")
            }
    )
    @PreAuthorize(value = "hasRole('USER')")
    @PutMapping("/close/v1/users/password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPassword updateUserPassword) {
        UserDTO updatedUser = userService.updatePassword(updateUserPassword, id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);
    }

}