package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/open/v1/categories")
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }


    @GetMapping("/open/v1/categories/{id}")
    public CategoryDTO getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }


    @PreAuthorize(value = "hasRole('ADMIN')")
    @PostMapping("/close/v1/categories")
    public CategoryDTO createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.save(categoryDTO);
    }


    @PreAuthorize(value = "hasRole('ADMIN')")
    @PutMapping("/close/v1/categories/{id}")
    public CategoryDTO updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.update(id, categoryDTO);
    }


    @PreAuthorize(value = "hasRole('ADMIN')")
    @DeleteMapping("/close/v1/categories/{id}")
    public CategoryDTO deleteCategory(@PathVariable Long id) {
        return categoryService.delete(id);
    }

}
