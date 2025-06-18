package uz.pdp.backend.olxapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.exception.UserNameAlreadyExistException;
import uz.pdp.backend.olxapp.payload.LoginDTO;
import uz.pdp.backend.olxapp.payload.RegisterDTO;
import uz.pdp.backend.olxapp.payload.TokenDTO;
import uz.pdp.backend.olxapp.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found"));

    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) {

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = (User) loadUserByUsername(username);

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            throw new RuntimeException("username or password incorrect");
        } else {
            return new TokenDTO(jwtService.generateToken(username));
        }


    }

    @Override
    public TokenDTO register(RegisterDTO registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserNameAlreadyExistException("username already exist");
        }

        User user = new User(
                registerDto.getFirstName(),
                registerDto.getLastName(),
                registerDto.getUsername(),
                passwordEncoder.encode(registerDto.getPassword()),
                registerDto.getEmail(),
                registerDto.getPhoneNumber(),
                Role.USER,
                null,
                null,
                null,
                null,
                null,
                null
        );

        User saveUser = userRepository.save(user);

        return new TokenDTO(jwtService.generateToken(saveUser.getUsername()));
    }
}
