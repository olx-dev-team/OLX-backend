package uz.pdp.backend.olxapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Favorites;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
}