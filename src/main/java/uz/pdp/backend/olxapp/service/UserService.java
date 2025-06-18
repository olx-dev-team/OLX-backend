package uz.pdp.backend.olxapp.service;

import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;
import uz.pdp.backend.olxapp.payload.LoginDTO;
import uz.pdp.backend.olxapp.payload.RegisterDTO;
import uz.pdp.backend.olxapp.payload.TokenDTO;

public interface UserService extends UserDetailsService {
    TokenDTO login(LoginDTO loginDTO);

    TokenDTO register(@Valid RegisterDTO registerDto);
}
