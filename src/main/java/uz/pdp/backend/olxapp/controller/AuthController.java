package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.payload.LoginDTO;
import uz.pdp.backend.olxapp.payload.RegisterDTO;
import uz.pdp.backend.olxapp.payload.TokenDTO;
import uz.pdp.backend.olxapp.service.UserService;
import uz.pdp.backend.olxapp.service.UserServiceImpl;

@RestController
@RequestMapping("/api/open/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    /**
     * Test case: Successful login
     * Input:
     * - Username: john_doe
     * - Password: password123
     * Expected output:
     * - Status code: 200 OK
     * - Response body contains a valid JWT token
     * Endpoint for logging in a user.
     *
     * @param loginDTO The DTO containing the username and password of the user to log in.
     * @return A JWT token representing the authenticated user's session.
     */
    @Operation(summary = "Login", description = "Authentication user with username and password")
    @PostMapping("/login")
    public TokenDTO login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginDTO.class),
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    value = "{ \"username\": \"john_doe\", \"password\": \"password123\" }"
                            )
                    )
            ) @Valid @RequestBody LoginDTO loginDTO) {

        return userService.login(loginDTO);

    }

    /**
     * TEST successful registration
     * input:
     * - First Name: John
     * - Last Name: Doe
     * - Username: johndoe@example.com
     * - Password: StrongP@ssw0rd!
     * expected output:
     * - status code : 200 ok
     * - response body contains a valid JWT token
     *
     * @param registerDto The DTO containing the registration details.
     * @return A JWT token representing the newly registered user.
     */
    @PostMapping("/register")
    public TokenDTO register(@Valid @RequestBody RegisterDTO registerDto) {

        return userService.register(registerDto);

    }
}
