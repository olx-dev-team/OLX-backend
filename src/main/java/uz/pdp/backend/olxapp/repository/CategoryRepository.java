package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}