package uz.pdp.backend.olxapp.controller;

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

    @PostMapping("/login")
    public TokenDTO login(@RequestBody LoginDTO loginDTO) {

        return userService.login(loginDTO);

    }

    @PostMapping("/register")
    public TokenDTO register(@RequestBody  @Valid RegisterDTO registerDto) {

        return userService.register(registerDto);

    }
}
