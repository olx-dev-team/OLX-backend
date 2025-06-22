package uz.pdp.backend.olxapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.CategoryMapper;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
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

        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND));
        return categoryMapper.toDto(category);

    }

    @Override
    public CategoryDTO save(CategoryDTO categoryDTO) {

        Category parentCategory = categoryRepository.findById(categoryDTO.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryDTO.getParentId(), HttpStatus.NOT_FOUND));

        Category category = new Category(
                categoryDTO.getName(),
                parentCategory,
                Collections.emptyList(),
                Collections.emptyList()
        );

        Category saveCategory = categoryRepository.save(category);
        return categoryMapper.toDto(saveCategory);

    }


    @Override
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND));


        category.setName(categoryDTO.getName());

        categoryRepository.save(category);
        return categoryMapper.toDto(category);

    }

    @Override
    public CategoryDTO delete(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id, HttpStatus.NOT_FOUND));

        categoryRepository.delete(category);
        return  categoryMapper.toDto(category);

    }
}
