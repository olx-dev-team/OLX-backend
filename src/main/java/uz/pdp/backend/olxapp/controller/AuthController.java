package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.LoginDTO;
import uz.pdp.backend.olxapp.payload.RegisterDTO;
import uz.pdp.backend.olxapp.payload.TokenDTO;
import uz.pdp.backend.olxapp.service.UserService;
import uz.pdp.backend.olxapp.service.UserServiceImpl;

@RestController
@RequestMapping("/api/open/auth")
@Tag(name = "Authentication", description = "Provides endpoints for user login and registration")
public class AuthController {

    private final UserService userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "User login endpoint",
            description = "Allows existing users to authenticate using valid credentials. Returns a JWT token on success.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Credentials required for login",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDTO.class),
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    summary = "Sample login credentials",
                                    value = "{ \"username\": \"john_doe\", \"password\": \"securePass123\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful, token returned"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @ApiResponse(responseCode = "400", description = "Missing or malformed request")
            }
    )
    @PostMapping("/login")
    public TokenDTO login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @Operation(
            summary = "User registration endpoint",
            description = "Allows new users to register an account. Requires username, password, and additional fields. Returns JWT token on success.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Details required to create a new user",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterDTO.class),
                            examples = @ExampleObject(
                                    name = "Registration Example",
                                    summary = "Sample user registration",
                                    value = "{ \"username\": \"new_user\", \"email\": \"user@example.com\", \"password\": \"strongPass456\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered and token returned"),
                    @ApiResponse(responseCode = "409", description = "User already exists"),
                    @ApiResponse(responseCode = "400", description = "Validation failed for input data")
            }
    )
    @PostMapping("/register")
    public TokenDTO register(@Valid @RequestBody RegisterDTO registerDto) {
        return userService.register(registerDto);
    }
}