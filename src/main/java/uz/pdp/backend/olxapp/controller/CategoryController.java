package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Category Controller", description = "Manage product categories")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * TEST successful
     *
     * @return [
     *   {
     *     "createdAt": "2023-04-18T15:39:46.796+00:00",
     *     "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *     "active": true,
     *     "id": 1,
     *     "name": "test"
     *   },
     *   {
     *     "createdAt": "2023-04-18T15:39:46.796+00:00",
     *     "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *     "active": true,
     *     "id": 2,
     *     "name": "test"
     *   }
     * ]
     * @return
     */
    @Operation(
            summary = "Get all categories",
            description = "Returns all categories (including parent and child categories)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/open/v1/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * TEST successful
     *
     * @param id - with category id
     * @return {
     *         "createdAt": "2023-04-18T15:39:46.796+00:00",
     *         "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *         "active": true,
     *         "id": 1,
     *         "name": "test"
     *       }
     */

    @Operation(
            summary = "Get category by ID",
            description = "Returns a category with the given ID",
            parameters = @Parameter(name = "id", description = "ID of the category"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category found",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/open/v1/categories/{id}")
    public CategoryDTO getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * TEST successful
     *
     * @param categoryReqDTO - {
     *                       "name": "test",
     *                       "parentId": null,
     *                       "active": true
     *                     }
     * @return {
     *         "createdAt": "2023-04-18T15:39:46.796+00:00",
     *         "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *         "active": true,
     *         "id": 1,
     *         "name": "test"
     *       }
     */
    @Operation(
            summary = "Create a new category",
            description = "Creates a new product category. Only ADMIN and MANAGER can access this endpoint.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Category object to be created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryReqDTO.class),
                            examples = @ExampleObject(
                                    name = "Category Create Example",
                                    value = """
                        {
                          "name": "Electronics",
                          "parentId": null,
                          "active": true
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/close/v1/categories")
    public CategoryDTO createCategory(@Valid @RequestBody CategoryReqDTO categoryReqDTO) {
        return categoryService.save(categoryReqDTO);
    }



    /**
     * TEST successful
     *
     * @param id             - with category id
     * @param categoryDTO    - {
     *                       "name": "test",
     *                       "parentId": null,
     *                       "active": true
     *                     }
     * @return {
     *         "createdAt": "2023-04-18T15:39:46.796+00:00",
     *         "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *         "active": true,
     *         "id": 1,
     *         "name": "test"
     *       }
     */
    @Operation(
            summary = "Update category",
            description = "Updates an existing category by ID. Only ADMIN and MANAGER roles are allowed.",
            parameters = @Parameter(name = "id", description = "ID of the category to update"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated category data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryReqDTO.class),
                            examples = @ExampleObject(
                                    name = "Update Example",
                                    value = """
                        {
                          "name": "Phones",
                          "parentId": 1,
                          "active": true
                        }
                        """
                            )
                    )
            )
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/close/v1/categories/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryReqDTO categoryDTO) {
        return categoryService.update(id, categoryDTO);
    }


    /**
     * TEST successful
     *
     * @param id - with category id
     * @return {
     *         "createdAt": "2023-04-18T15:39:46.796+00:00",
     *         "updatedAt": "2023-04-18T15:39:46.796+00:00",
     *         "active": true,
     *         "id": 1,
     *         "name": "test"
     *       }
     */
    @Operation(
            summary = "Delete category",
            description = "Deletes a category by ID. Only ADMIN and MANAGER roles are allowed.",
            parameters = @Parameter(name = "id", description = "ID of the category to delete"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/close/v1/categories/{id}")
    public CategoryDTO deleteCategory(@PathVariable Long id) {
        return categoryService.delete(id);
    }
}
