package uz.pdp.backend.olxapp.service;

import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;
import uz.pdp.backend.olxapp.payload.*;

public interface UserService extends UserDetailsService {
    TokenDTO login(LoginDTO loginDTO);

    TokenDTO register(@Valid RegisterDTO registerDto);

    PageDTO<UserDTO> getAll(Integer page, Integer size);

    PageDTO<?> getAllInactive(Integer page, Integer size);

    void changeUserRole(Long id, ChangeUserRole changeUserRole);

    UserDTO getById(Long id);

    UserDTO updateUser(UpdateUser updateUser, Long id);

    UserDTO updatePassword(UpdateUserPassword updateUserPassword, Long id);

    ApiResponse resetPassword(@Valid ResetPasswordDTO resetPasswordDTO);

    TokenDTO resetPasswordByToken(@Valid TokenDTO resetPasswordByTokenDTO);

    void changeNewPassword(@Valid NewPasswordDTO newPassword);

}
