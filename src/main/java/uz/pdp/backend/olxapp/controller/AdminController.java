package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.UserService;

@RestController
@RequestMapping("/api/close/v1/admin")
@RequiredArgsConstructor
public class AdminController {

//    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/get-all-users")
    public PageDTO<?> getAllUsers(@RequestParam Integer page,
                                  @RequestParam Integer size){
        return userService.getAll(page,size);
    }

    @GetMapping("/get-all-users/inactive")
    public PageDTO<?> getAllInactiveUsers(@RequestParam Integer page,
                                          @RequestParam Integer size){
        return userService.getAllInactive(page,size);
    }

//    @GetMapping("/")
}
