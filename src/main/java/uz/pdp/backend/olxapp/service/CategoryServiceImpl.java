package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.exception.ConflictException;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.CategoryMapper;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.payload.CategoryReqDTO;
import uz.pdp.backend.olxapp.repository.CategoryRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {

        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toDto).toList();

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);
        return categoryMapper.toDto(category);

    }

    @Override
    public CategoryDTO save(CategoryReqDTO categoryReqDTO) {

        Category parentCategory = null;
        if (categoryReqDTO.getParentId() != 0) {
            parentCategory = categoryRepository.findByIdOrThrow(categoryReqDTO.getParentId());
        }

        if (categoryRepository.existsByNameIgnoreCase(categoryReqDTO.getName())) {
            throw new ConflictException("Category name already exists: " + categoryReqDTO.getName(), HttpStatus.CONFLICT);
        }

        Category category = new Category(
                categoryReqDTO.getName(),
                parentCategory,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Category saveCategory = categoryRepository.save(category);
        return categoryMapper.toDto(saveCategory);

    }


    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {

        Category category = categoryRepository.findByIdOrThrow(id);

        category.setName(categoryDTO.getName());

        categoryRepository.save(category);
        return categoryMapper.toDto(category);

    }

    @Override
    public CategoryDTO delete(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);

        categoryRepository.delete(category);
        return categoryMapper.toDto(category);

    }
}
