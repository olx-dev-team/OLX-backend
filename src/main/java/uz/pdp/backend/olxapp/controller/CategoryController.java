package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.payload.CategoryReqDTO;
import uz.pdp.backend.olxapp.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Category", description = "Endpoints for managing categories, including creation, update, retrieval and deletion")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get all categories",
            description = "Retrieves the list of all categories available in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/open/v1/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(
            summary = "Get category by ID",
            description = "Fetches a single category based on the provided ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category found"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/open/v1/categories/{id}")
    public CategoryDTO getCategoryById(
            @PathVariable
            @Schema(description = "ID of the category to retrieve", example = "1")
            Long id) {
        return categoryService.getCategoryById(id);
    }

    @Operation(
            summary = "Create a new category",
            description = "Allows ADMIN to create a new category. Requires a valid CategoryReqDTO object in the request body.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Category details",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryReqDTO.class),
                            examples = @ExampleObject(
                                    name = "New Category",
                                    summary = "Example category creation",
                                    value = "{ \"name\": \"Electronics\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping("/close/v1/categories")
    public CategoryDTO createCategory(@RequestBody @Valid CategoryReqDTO categoryReqDTO) {
        return categoryService.save(categoryReqDTO);
    }

    @Operation(
            summary = "Update existing category",
            description = "Updates the details of an existing category. Only accessible to ADMIN.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Updated category data",
                    content = @Content(schema = @Schema(implementation = CategoryDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid update data")
            }
    )
    @PreAuthorize(value = "hasRole('ADMIN')")
    @PutMapping("/close/v1/categories/{id}")
    public CategoryDTO updateCategory(
            @PathVariable
            @Schema(description = "ID of the category to update", example = "2")
            Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(id, categoryDTO);
    }

    @Operation(
            summary = "Delete a category",
            description = "Deletes the category by its ID. Only ADMINs are allowed to perform this operation.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PreAuthorize(value = "hasRole('ADMIN')")
    @DeleteMapping("/close/v1/categories/{id}")
    public CategoryDTO deleteCategory(
            @PathVariable
            @Schema(description = "ID of the category to delete", example = "3")
            Long id) {
        return categoryService.delete(id);
    }
}