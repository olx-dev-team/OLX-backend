package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.CategoryMapper;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.payload.CategoryReqDTO;
import uz.pdp.backend.olxapp.repository.CategoryRepository;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        log.info("Fetching all categories");
        List<Category> categories = categoryRepository.findAll();
        log.info("Fetched {} categories", categories.size());
        return categories.stream().map(categoryMapper::toDto).toList();
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", id);
                    return new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND);
                });
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDTO save(CategoryReqDTO categoryReqDTO) {
        log.info("Saving new category: {}", categoryReqDTO.getName());

        Category parentCategory = null;
        if (categoryReqDTO.getParentId() != 0) {
            log.info("Category has a parent with ID: {}", categoryReqDTO.getParentId());
            parentCategory = categoryRepository.findById(categoryReqDTO.getParentId())
                    .orElseThrow(() -> {
                        log.warn("Parent category not found with ID: {}", categoryReqDTO.getParentId());
                        return new EntityNotFoundException("Category not found with id: " + categoryReqDTO.getParentId(), HttpStatus.NOT_FOUND);
                    });
        }

        Category category = new Category(
                categoryReqDTO.getName(),
                parentCategory,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Category saveCategory = categoryRepository.save(category);
        log.info("Saved category with ID: {}", saveCategory.getId());
        return categoryMapper.toDto(saveCategory);
    }


    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        log.info("Updating category ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", id);
                    return new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND);
                });

        category.setName(categoryDTO.getName());

        categoryRepository.save(category);
        log.info("Updated category ID: {}", category.getId());
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDTO delete(Long id) {
        log.info("Deleting category ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with ID: {}", id);
                    return new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND);
                });

        categoryRepository.delete(category);
        log.info("Deleted category ID: {}", category.getId());
        return categoryMapper.toDto(category);
    }
}
