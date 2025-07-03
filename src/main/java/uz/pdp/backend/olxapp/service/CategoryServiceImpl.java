package uz.pdp.backend.olxapp.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.exception.ConflictException;
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

        log.info("Getting all categories");
        List<Category> categories = categoryRepository.getCategory();
        return categories.stream().map(categoryMapper::toDto).toList();

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {

        Category category = categoryRepository.findByIdOrThrow(id);
        return categoryMapper.toDto(category);

    }

    @Override
    @Transactional
    public CategoryDTO save(CategoryReqDTO categoryReqDTO) {
        log.info("Saving new category with name: {}", categoryReqDTO.getName());

        Category parentCategory = null;
        if (categoryReqDTO.getParentId() != 0) {
            parentCategory = categoryRepository.findByIdOrThrow(categoryReqDTO.getParentId());
            log.debug("Parent category found: ID = {}", categoryReqDTO.getParentId());
        }

        if (categoryRepository.existsByNameIgnoreCase(categoryReqDTO.getName())) {
            log.warn("Attempt to create category with duplicate name: {}", categoryReqDTO.getName());
            throw new ConflictException("Category name already exists: " + categoryReqDTO.getName(), HttpStatus.CONFLICT);
        }

        Category category = new Category(
                categoryReqDTO.getName(),
                parentCategory,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Category savedCategory = categoryRepository.save(category);
        log.info("Category saved successfully with ID: {}", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO update(Long id, @Valid CategoryReqDTO categoryDTO) {
        log.info("Updating category ID: {}", id);
        Category category = categoryRepository.findByIdOrThrow(id);
        log.debug("Current category name: '{}', updating to '{}'", category.getName(), categoryDTO.getName());

        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null ) {
            Category newParentCategory = categoryRepository.findByIdOrThrow(categoryDTO.getParentId());
            log.debug("Changing parent category from '{}' to '{}'", category.getParent(), newParentCategory);
            category.setParent(newParentCategory);
        } else {
            log.debug("No changes in the parent category.");
            category.setParent(null); // Resetting the parent category
        }
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO delete(Long id) {
        log.info("Attempting to delete category ID: {}", id);
        Category category = categoryRepository.findByIdOrThrow(id);

        if (!category.getChildren().isEmpty()) {
            log.warn("Cannot delete category ID {} because it has child categories", id);
            throw new EntityNotFoundException("Can't delete category with child categories!", HttpStatus.CONFLICT);
        }

        if (!category.getProducts().isEmpty()) {
            log.warn("Cannot delete category ID {} because it contains products", id);
            throw new EntityNotFoundException("Can't delete category with products!", HttpStatus.CONFLICT);
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: ID = {}", id);
        return categoryMapper.toDto(category);
    }
}
