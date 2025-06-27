package uz.pdp.backend.olxapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.payload.CategoryDTO;
import uz.pdp.backend.olxapp.repository.CategoryRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Avazbek on 18/06/25 17:22
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    void test() {
//        importRecursiveJsonToDb("src/main/resources/docs/sql/olx_full_categories.json");
    }


    public void importRecursiveJsonToDb(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream is = new FileInputStream(filePath)) {
            List<CategoryDTO> topLevelList = Arrays.asList(objectMapper.readValue(is, CategoryDTO[].class));

            Map<Long, Category> savedMap = new HashMap<>();

            importRecursively(topLevelList, null, savedMap);

            System.out.println("All categories imported recursively!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read or parse category JSON file", e);
        }
    }

    private void importRecursively(List<CategoryDTO> dtoList, Category parent, Map<Long, Category> savedMap) {
        for (CategoryDTO dto : dtoList) {
            Category category = new Category();
            category.setName(dto.getName());
            category.setParent(parent);

            Category saved = categoryRepository.save(category);
            savedMap.put(dto.getId(), saved);

            if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
                importRecursively(dto.getChildren(), saved, savedMap);
            }
        }
    }


}
