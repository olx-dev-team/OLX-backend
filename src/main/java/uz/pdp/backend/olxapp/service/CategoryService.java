package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.payload.CategoryReqDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(Long id);

    CategoryDTO save(CategoryReqDTO categoryReqDTO);

    CategoryDTO update(Long id, CategoryDTO categoryDTO);

    CategoryDTO delete(Long id);
}
