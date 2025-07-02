package uz.pdp.backend.olxapp.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "name ") String name);

    default Category findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(
                "Category not found with id: " + id,
                HttpStatus.NOT_FOUND
        ));
    }
    @Query("select c from Category c where c.parent is null")
    List<Category> getCategory();
}