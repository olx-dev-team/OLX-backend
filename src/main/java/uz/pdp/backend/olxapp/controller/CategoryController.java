package uz.pdp.backend.olxapp.controller;

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
    @PreAuthorize(value = "hasAnyRole('ADMIN','MANAGER')")
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
    @PreAuthorize(value = "hasAnyRole('ADMIN','MANAGER')")
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
    @PreAuthorize(value = "hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/close/v1/categories/{id}")
    public CategoryDTO deleteCategory(@PathVariable Long id) {
        return categoryService.delete(id);
    }

}
