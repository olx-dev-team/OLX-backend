package uz.pdp.backend.olxapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.backend.olxapp.entity.Favorites;
import uz.pdp.backend.olxapp.entity.User;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    Page<Favorites> findByUser(User user, Pageable pageRequest);

    Optional<Favorites> findByUserIdAndProductId(Long userId, Long productId);
}