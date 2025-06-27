package uz.pdp.backend.olxapp.service;

import jakarta.transaction.Transactional;
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

        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = (User) loadUserByUsername(username);

        boolean matches = passwordEncoder.matches(password, user.getPassword());
        if (!matches) {
            throw new BadCredentialsException("username or password incorrect");
        } else {
            return new TokenDTO(jwtService.generateToken(username));
        }


    }

    @Override
    public TokenDTO register(RegisterDTO registerDto) {

        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new UserNameAlreadyExistException("username already exist");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
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

        Page<User> pageResult = userRepository.findByActiveFalse(false, pageRequest);

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

        User user = userRepository.findByIdOrThrow(id);

        if (changeUserRole.getRole() == null || !user.getRole().equals(changeUserRole.getRole())) {
            user.setRole(changeUserRole.getRole());
        } else {
            throw new IllegalActionException("User's role cannot be changed to the same role", HttpStatus.BAD_REQUEST);
        }

        if (changeUserRole.getActive() != null) {
            user.setActive(changeUserRole.getActive());
        }
        userRepository.save(user);
    }

    @Override
    public UserDTO getById(Long id) {

        User user = userRepository.findByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updateUser(UpdateUser updateUser, Long id) {
        User user = userRepository.findByIdOrThrow(id);

        // Username o'zgartirilgan bo'lsa
        if (!updateUser.getUsername().equals(user.getUsername())) {
            // Va yangi username boshqa userga tegishli bo'lsa
            if (userRepository.existsByUsername(updateUser.getUsername())) {
                throw new UserNameAlreadyExistException("Username already exists");
            }
            user.setUsername(updateUser.getUsername());
        }

        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setEmail(updateUser.getEmail());
        user.setPhoneNumber(updateUser.getPhoneNumber());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updatePassword(UpdateUserPassword updateUserPassword, Long id) {
        User user = userRepository.findByIdOrThrow(id);

        String oldPassword = updateUserPassword.getOldPassword();
        String newPassword = updateUserPassword.getNewPassword();
        String confirmPassword = updateUserPassword.getConfirmPassword();


        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordMismatchException("New password and confirm password must match", HttpStatus.BAD_REQUEST);
        }

        if (oldPassword.equals(newPassword)) {
            throw new SamePasswordException("New password must not be the same as the old password", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IncorrectOldPasswordException("Old password is incorrect", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByEmail(resetPasswordDTO.getEmail())
                .orElseThrow(() -> new IllegalActionException(
                        ResponseStatusEnum.EMAIL_NOT_FOUND.getMessage(),
                        ResponseStatusEnum.EMAIL_NOT_FOUND.getHttpStatus()));

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
            } else {
                return new ApiResponse(false,
                        ResponseStatusEnum.TOKEN_ALREADY_SENT.getMessage(),
                        ResponseStatusEnum.TOKEN_ALREADY_SENT.getHttpStatus().value());
            }
        } else {
            resetToken = new PasswordResetToken(null, token, user, expiry);
            shouldSend = true;
        }

        passwordResetTokenRepository.save(resetToken);

        try {
            Boolean sent = emailService.sendSimpleEmail(
                    user.getEmail(),
                    "Password Reset Code",
                    "Click the link below to reset your password:\n" + token
            ).get();

            if (!sent) {
                throw new IllegalActionException(
                        ResponseStatusEnum.TOKEN_SEND_FAILED.getMessage(),
                        ResponseStatusEnum.TOKEN_SEND_FAILED.getHttpStatus());
            }

            return new ApiResponse(true,
                    ResponseStatusEnum.TOKEN_SENT.getMessage(),
                    ResponseStatusEnum.TOKEN_SENT.getHttpStatus().value());

        } catch (Exception e) {
            throw new IllegalActionException(
                    ResponseStatusEnum.INTERNAL_ERROR.getMessage(),
                    ResponseStatusEnum.INTERNAL_ERROR.getHttpStatus());
        }

    }


    @Override
    public TokenDTO resetPasswordByToken(TokenDTO resetPasswordByTokenDTO) {

        PasswordResetToken tokenIsInvalidOrExpired = passwordResetTokenRepository.findByToken(resetPasswordByTokenDTO.getToken())
                .orElseThrow(() -> new EntityNotFoundException("Token is invalid or expired", HttpStatus.NOT_FOUND));

        LocalDateTime expiryDate = tokenIsInvalidOrExpired.getExpiryDate();
        if (expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalActionException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        return resetPasswordByTokenDTO;

    }

    @Override
    public void changeNewPassword(NewPasswordDTO passwordDTO) {

        String newPassword = passwordDTO.getNewPassword();
        String confirmPassword = passwordDTO.getConfirmPassword();
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalActionException("New password and confirm password must match", HttpStatus.BAD_REQUEST);
        }

        String token = passwordDTO.getToken();

        PasswordResetToken tokenIsInvalidOrExpired = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Token is invalid or expired", HttpStatus.NOT_FOUND));

        LocalDateTime expiryDate = tokenIsInvalidOrExpired.getExpiryDate();
        if (expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalActionException("Token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = tokenIsInvalidOrExpired.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.deleteById(tokenIsInvalidOrExpired.getId());


    }
}
