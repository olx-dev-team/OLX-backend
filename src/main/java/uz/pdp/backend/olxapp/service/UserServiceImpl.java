package uz.pdp.backend.olxapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.exception.*;
import uz.pdp.backend.olxapp.mapper.UserMapper;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new RuntimeException("user not found");
                });
    }


    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        log.info("Attempting login for username: {}", loginDTO.getUsername());

        User user = (User) loadUserByUsername(loginDTO.getUsername());
        boolean matches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());

        if (!matches) {
            log.warn("Login failed for user: {}", loginDTO.getUsername());
            throw new RuntimeException("username or password incorrect");
        }

        log.info("Login successful for user: {}", user.getUsername());
        return new TokenDTO(jwtService.generateToken(user.getUsername()));
    }

    @Override
    public TokenDTO register(RegisterDTO registerDto) {

        log.info("Registering new user with username: {}", registerDto.getUsername());

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            log.warn("Username already exists: {}", registerDto.getUsername());
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
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        User saveUser = userRepository.save(user);

        log.info("User registered successfully: {}", saveUser.getUsername());

        return new TokenDTO(jwtService.generateToken(saveUser.getUsername()));
    }


    @Override
    public PageDTO<UserDTO> getAll(Integer page, Integer size) {

        log.info("Fetching all users. Page: {}, Size: {}", page, size);

        Sort sort = Sort.by(LongIdAbstract.Fields.id).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> pageResult = userRepository.findAll(pageRequest);

        log.info("Fetched {} users from database", pageResult.getTotalElements());

        List<UserDTO> content = pageResult.stream()
                .map(userMapper::toDto)
                .toList();

        return new PageDTO<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.isLast(),
                pageResult.isFirst(),
                pageResult.getNumberOfElements(),
                pageResult.isEmpty()
        );
    }

    @Override
    public UserDTO getById(Long id) {

        log.info("Fetching user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new EntityNotFoundException("user not found with id: %s".formatted(id), HttpStatus.NOT_FOUND);
                });

        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updateUser(UpdateUser updateUser, Long id) {

        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new EntityNotFoundException("user not found with id: %s".formatted(id), HttpStatus.NOT_FOUND);
                });

        // Username o'zgartirilgan bo'lsa
        if (!updateUser.getUsername().equals(user.getUsername())) {
            // Va yangi username boshqa userga tegishli bo'lsa
            if (userRepository.existsByUsername(updateUser.getUsername())) {
                log.warn("Username already taken: {}", updateUser.getUsername());
                throw new UserNameAlreadyExistException("Username already exists");
            }
            log.info("Username changed from {} to {}", user.getUsername(), updateUser.getUsername());
            user.setUsername(updateUser.getUsername());
        }

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setEmail(updateUser.getEmail());
        user.setPhoneNumber(updateUser.getPhoneNumber());

        userRepository.save(user);

        log.info("User updated successfully: {}", user.getUsername());

        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updatePassword(UpdateUserPassword updateUserPassword, Long id) {

        log.info("Updating password for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new EntityNotFoundException(
                        "User not found with id: %s".formatted(id), HttpStatus.NOT_FOUND);
                });

        String oldPassword = updateUserPassword.getOldPassword();
        String newPassword = updateUserPassword.getNewPassword();
        String confirmPassword = updateUserPassword.getConfirmPassword();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Incorrect old password for user ID: {}", id);
            throw new IncorrectOldPasswordException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        if (!newPassword.equals(confirmPassword)) {
            log.warn("Password mismatch for user ID: {}", id);
            throw new PasswordMismatchException("New password and confirm password must match", HttpStatus.BAD_REQUEST);
        }

        if (oldPassword.equals(newPassword)) {
            log.warn("New password same as old password for user ID: {}", id);
            throw new SamePasswordException("New password must not be the same as the old password", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password updated successfully for user ID: {}", id);

        return userMapper.toDto(user);
    }


}
