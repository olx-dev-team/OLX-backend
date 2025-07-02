package uz.pdp.backend.olxapp.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.entity.PasswordResetToken;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.entity.abstractEntity.LongIdAbstract;
import uz.pdp.backend.olxapp.enums.ResponseStatusEnum;
import uz.pdp.backend.olxapp.enums.Role;
import uz.pdp.backend.olxapp.exception.*;
import uz.pdp.backend.olxapp.mapper.UserMapper;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.repository.PasswordResetTokenRepository;
import uz.pdp.backend.olxapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("user not found"));

    }

    @Override
    public TokenDTO login(LoginDTO loginDTO) {
        log.info("User '{}' attempting to log in", loginDTO.getUsername());

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = (User) loadUserByUsername(username);

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            log.warn("Failed login attempt for user '{}': bad credentials", username);
            throw new BadCredentialsException("username or password incorrect");
        }

        log.info("User '{}' successfully logged in", username);
        return new TokenDTO(jwtService.generateToken(username));
    }

    @Override
    public TokenDTO register(RegisterDTO registerDto) {

        log.info("Registering new user: {}", registerDto.getUsername());

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            log.warn("Username '{}' already exists", registerDto.getUsername());
            throw new UserNameAlreadyExistException("username already exist");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            log.warn("Email '{}' already exists", registerDto.getEmail());
            throw new ConflictException("email already exist", HttpStatus.CONFLICT);
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

        log.info("User '{}' registered successfully", saveUser.getUsername());

        return new TokenDTO(jwtService.generateToken(saveUser.getUsername()));
    }


    @Override
    public PageDTO<UserDTO> getAll(Integer page, Integer size) {
        Sort sort = Sort.by(LongIdAbstract.Fields.id).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> pageResult = userRepository.findAll(pageRequest);
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
    public PageDTO<?> getAllInactive(Integer page, Integer size) {

        Sort sort = Sort.by(LongIdAbstract.Fields.id).descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> pageResult = userRepository.getUsersByActive(false, pageRequest);

        return new PageDTO<>(
                pageResult.getContent().stream().map(userMapper::toDto).toList(),
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
    public void changeUserRole(Long id, ChangeUserRole changeUserRole) {
        log.info("Attempting to change role for user with ID {}", id);

        User user = userRepository.findByIdOrThrow(id);

        if (changeUserRole.getRole() == null || !user.getRole().equals(changeUserRole.getRole())) {
            log.info("Changing role for user ID {} from {} to {}", id, user.getRole(), changeUserRole.getRole());
            user.setRole(changeUserRole.getRole());
        } else {
            log.warn("Attempted to assign the same role '{}' to user ID {}", user.getRole(), id);
            throw new IllegalActionException("User's role cannot be changed to the same role", HttpStatus.BAD_REQUEST);
        }

        if (changeUserRole.getActive() != null) {
            log.info("Setting active status for user ID {} to {}", id, changeUserRole.getActive());
            user.setActive(changeUserRole.getActive());
        }

        userRepository.save(user);
        log.info("User ID {} updated successfully", id);
    }

    @Override
    public UserDTO getById(Long id) {

        User user = userRepository.findByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updateUser(UpdateUser updateUser, Long id) {
        log.info("Starting update for user ID {}", id);

        User user = userRepository.findByIdOrThrow(id);

        // Проверка изменения имени пользователя
        if (!updateUser.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateUser.getUsername())) {
                log.warn("Attempt to change username to an existing one: {}", updateUser.getUsername());
                throw new UserNameAlreadyExistException("Username already exists");
            }
            log.info("Changing username from '{}' to '{}'", user.getUsername(), updateUser.getUsername());
            user.setUsername(updateUser.getUsername());
        }

        // Обновление остальных полей
        log.info("Updating fields for user ID {}: firstName={}, lastName={}, email={}, phone={}",
                id, updateUser.getFirstName(), updateUser.getLastName(),
                updateUser.getEmail(), updateUser.getPhoneNumber());

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setEmail(updateUser.getEmail());
        user.setPhoneNumber(updateUser.getPhoneNumber());

        userRepository.save(user);

        log.info("Successfully updated user ID {}", id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updatePassword(UpdateUserPassword updateUserPassword, Long id) {
        log.info("Attempting to update password for user ID {}", id);

        User user = userRepository.findByIdOrThrow(id);

        String oldPassword = updateUserPassword.getOldPassword();
        String newPassword = updateUserPassword.getNewPassword();
        String confirmPassword = updateUserPassword.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            log.warn("Password confirmation mismatch for user ID {}", id);
            throw new PasswordMismatchException("New password and confirm password must match", HttpStatus.BAD_REQUEST);
        }

        if (oldPassword.equals(newPassword)) {
            log.warn("User ID {} tried to reuse old password", id);
            throw new SamePasswordException("New password must not be the same as the old password", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Incorrect old password for user ID {}", id);
            throw new IncorrectOldPasswordException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password successfully updated for user ID {}", id);
        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String email = resetPasswordDTO.getEmail();
        log.info("Initiating password reset for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: email '{}' not found", email);
                    return new IllegalActionException("Email not found", HttpStatus.NOT_FOUND);
                });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByUser(user);
        boolean shouldSend = false;
        PasswordResetToken resetToken;

        if (optionalToken.isPresent()) {
            resetToken = optionalToken.get();
            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                resetToken.setToken(token);
                resetToken.setExpiryDate(expiry);
                shouldSend = true;
                log.info("Existing token expired. Generating new token for email: {}", email);
            } else {
                log.warn("Reset token for email '{}' was already sent recently", email);
                return new ApiResponse(false,
                        ResponseStatusEnum.TOKEN_ALREADY_SENT.getMessage(),
                        ResponseStatusEnum.TOKEN_ALREADY_SENT.getHttpStatus().value());
            }
        } else {
            resetToken = new PasswordResetToken(null, token, user, expiry);
            shouldSend = true;
            log.info("Creating new reset token for email: {}", email);
        }

        passwordResetTokenRepository.save(resetToken);

        try {
            Boolean sent = emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Password Reset Code",
                    "Click the link below to reset your password:\n" + token
            ).get();

            if (!sent) {
                log.error("Failed to send reset email to '{}'", email);
                throw new IllegalActionException(
                        ResponseStatusEnum.TOKEN_SEND_FAILED.getMessage(),
                        ResponseStatusEnum.TOKEN_SEND_FAILED.getHttpStatus());
            }

            log.info("Reset token successfully sent to email: {}", email);
            return new ApiResponse(true,
                    ResponseStatusEnum.TOKEN_SENT.getMessage(),
                    ResponseStatusEnum.TOKEN_SENT.getHttpStatus().value());

        } catch (Exception e) {
            log.error("Exception while sending reset email to '{}': {}", email, e.getMessage(), e);
            throw new IllegalActionException(
                    ResponseStatusEnum.INTERNAL_ERROR.getMessage(),
                    ResponseStatusEnum.INTERNAL_ERROR.getHttpStatus());
        }
    }

    @Override
    public TokenDTO resetPasswordByToken(TokenDTO resetPasswordByTokenDTO) {
        String token = resetPasswordByTokenDTO.getToken();
        log.info("Validating password reset token: {}", token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Password reset token not found or invalid: {}", token);
                    return new EntityNotFoundException("Token is invalid or expired", HttpStatus.NOT_FOUND);
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Password reset token expired: {}", token);
            throw new IllegalActionException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        log.info("Password reset token is valid: {}", token);
        return resetPasswordByTokenDTO;
    }

    @Override
    public void changeNewPassword(NewPasswordDTO passwordDTO) {
        String token = passwordDTO.getToken();
        String newPassword = passwordDTO.getNewPassword();
        String confirmPassword = passwordDTO.getConfirmPassword();

        log.info("Attempting to change password using token: {}", token);

        if (!newPassword.equals(confirmPassword)) {
            log.warn("Password mismatch for token: {}", token);
            throw new IllegalActionException("New password and confirm password must match", HttpStatus.BAD_REQUEST);
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired token: {}", token);
                    return new EntityNotFoundException("Token is invalid or expired", HttpStatus.NOT_FOUND);
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Token has expired: {}", token);
            throw new IllegalActionException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = resetToken.getUser();
        log.info("Resetting password for user: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.deleteById(resetToken.getId());
        log.info("Password successfully updated and token removed for user: {}", user.getUsername());
    }
}
