package uz.pdp.backend.olxapp.service;

import jakarta.validation.Valid;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.payload.CategoryReqDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO save(CategoryReqDTO categoryReqDTO);

    CategoryDTO update(Long id, @Valid CategoryReqDTO categoryDTO);

    CategoryDTO delete(Long id);
}
