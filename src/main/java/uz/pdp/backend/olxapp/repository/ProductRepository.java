package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}