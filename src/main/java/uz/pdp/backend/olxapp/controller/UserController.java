package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.config.JwtService;
import uz.pdp.backend.olxapp.payload.*;
import uz.pdp.backend.olxapp.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @PreAuthorize(value = "hasAnyRole('ADMIN')")
    @GetMapping("/close/v1/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {

        PageDTO<UserDTO> userDTOPage = userService.getAll(page, size);
        return ResponseEntity.ok(userDTOPage);
    }

    @PreAuthorize(value = "hasAnyRole('ADMIN')")
    @GetMapping("/close/v1/users/inactive")
    public PageDTO<?> getAllInactiveUsers(@RequestParam Integer page,
                                          @RequestParam Integer size){
        return userService.getAllInactive(page,size);
    }

    @GetMapping("/open/v1/users/changed/{id}")
    public void changeUserRole(@PathVariable Long id,@RequestBody ChangeUserRole changeUserRole){
        userService.changeUserRole(id,changeUserRole);
    }

    @PreAuthorize(value = "hasAnyRole('USER','ADMIN')")
    @GetMapping("/close/v1/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {

        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.ok(userDTO);

    }

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

    @PreAuthorize(value = "hasRole('USER')")
    @PutMapping("/close/v1/users/password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdateUserPassword updateUserPassword) {

        UserDTO updatedUser = userService.updatePassword(updateUserPassword, id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);

    }

    @PutMapping("/open/v1/users/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        ApiResponse apiResponse = userService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/open/v1/users/reset-password/token")
    public ResponseEntity<?> resetPasswordByToken(@RequestBody @Valid TokenDTO resetPasswordByTokenDTO) {
        TokenDTO tokenDTO = userService.resetPasswordByToken(resetPasswordByTokenDTO);
        return ResponseEntity.ok(tokenDTO);
    }

    @PostMapping("/open/v1/users/reset-password/new-password")
    public ResponseEntity<?> changeNewPassword(@RequestBody @Valid NewPasswordDTO newPassword) {
        userService.changeNewPassword(newPassword);
        return ResponseEntity.ok("Successfully changed your password");
    }


}
