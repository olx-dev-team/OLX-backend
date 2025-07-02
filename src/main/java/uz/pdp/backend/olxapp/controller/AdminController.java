package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/close/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "Endpoints for admin operations")
public class AdminController {

//    private final AdminService adminService;
    private final UserService userService;

    /**
     * Test successfully
     * @param page - default value is zero
     * @param size - default value is ten
     * @return all users with pagination
     */
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all users. Accessible only by ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied – only ADMIN role allowed")
    })
    @GetMapping("/get-all-users")
    public PageDTO<?> getAllUsers(
            @Parameter(description = "Page number (starting from 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Number of users per page", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAll(page, size);
    }


    /**
     * Test successfully
     * @param page - default value is zero
     * @param size - default value is ten
     * @return all inactive users with pagination
     */
    @Operation(
            summary = "Get all inactive users",
            description = "Returns a paginated list of users who are currently inactive. Accessible only by ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inactive users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied – only ADMIN role allowed")
    })
    @GetMapping("/get-all-users/inactive")
    public PageDTO<?> getAllInactiveUsers(
            @Parameter(description = "Page number (starting from 0)", example = "0")
            @RequestParam(defaultValue = "0") Integer page,

            @Parameter(description = "Number of users per page", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAllInactive(page, size);
    }

//    @GetMapping("/")
}
