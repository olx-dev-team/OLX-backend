package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.UserService;

@RestController
@RequestMapping("/api/close/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

//    private final AdminService adminService;
    private final UserService userService;

    /**
     * Test successfully
     * @param page - default value is zero
     * @param size - default value is ten
     * @return all users with pagination
     */
    @GetMapping("/get-all-users")
    public PageDTO<?> getAllUsers(@RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size){
        return userService.getAll(page,size);
    }

    /**
     * Test successfully
     * @param page - default value is zero
     * @param size - default value is ten
     * @return all inactive users with pagination
     */
    @GetMapping("/get-all-users/inactive")
    public PageDTO<?> getAllInactiveUsers(@RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size){
        return userService.getAllInactive(page,size);
    }

//    @GetMapping("/")
}
