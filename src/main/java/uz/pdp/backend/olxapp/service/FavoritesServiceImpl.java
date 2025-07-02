package uz.pdp.backend.olxapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.backend.olxapp.entity.Favorites;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.entity.abstractEntity.AbstractEntity;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.FavoritesMapper;
import uz.pdp.backend.olxapp.payload.FavoriteReqDTO;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.repository.FavoritesRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final FavoritesMapper favoritesMapper;
    private final ProductRepository productRepository;

    @Override
    public PageDTO<FavoritesDTO> getAllFavorites(Integer page, Integer size) {

        log.info("Fetching all favorites. Page: {}, Size: {}", page, size);

        Sort sort = Sort.by(AbstractEntity.Fields.createdAt);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthorized access attempt to favorites");
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Favorites> favoritesPage = favoritesRepository.findByUser(user, pageRequest);
        log.info("Fetched {} favorites for user ID {}", favoritesPage.getTotalElements(), user.getId());

        return new PageDTO<>(
                favoritesPage.getContent().stream().map(favoritesMapper::toDto).toList(),
                favoritesPage.getNumber(),
                favoritesPage.getSize(),
                favoritesPage.getTotalElements(),
                favoritesPage.getTotalPages(),
                favoritesPage.isLast(),
                favoritesPage.isFirst(),
                favoritesPage.getNumberOfElements(),
                favoritesPage.isEmpty()
        );
    }

    @Override
    public FavoritesDTO getByIdFavorites(Long id) {
        log.info("Fetching favorite by ID: {}", id);

        Favorites favorites = favoritesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorite not found with ID: {}", id);
                    return new EntityNotFoundException("Favorites with id " + id + " not found", HttpStatus.NOT_FOUND);
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthorized user tried to access favorite ID: {}", id);
            throw new AccessDeniedException("User is not authenticated");
        }

        if (!favorites.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} tried to access favorite owned by user ID {}", user.getId(), favorites.getUser().getId());
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        return favoritesMapper.toDto(favorites);
    }


    @Override
    public String addFavorite(FavoriteReqDTO favoriteReqDTO) {

        log.info("Toggling favorite for product ID: {}", favoriteReqDTO.getProductId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthorized user tried to add/remove favorite");
            throw new AccessDeniedException("User is not authenticated");
        }

        Product product = productRepository.findById(favoriteReqDTO.getProductId())
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", favoriteReqDTO.getProductId());
                    return new EntityNotFoundException("Product with id " + favoriteReqDTO.getProductId() + " not found", HttpStatus.NOT_FOUND);
                });
        Optional<Favorites> optionalFavorite = favoritesRepository.findByUserAndProduct(user, product);

        if (optionalFavorite.isPresent()) {

           favoritesRepository.deleteById(optionalFavorite.get().getId());

            log.info("Removed favorite for user ID {} and product ID {}", user.getId(), product.getId());

            return "Product removed from favorites";

        } else {

            Favorites favorites = new Favorites(
                    user,
                    product
            );

            favoritesRepository.save(favorites);

            log.info("Added favorite for user ID {} and product ID {}", user.getId(), product.getId());

            return "Product added to favorites";
        }

    }

    @Override
    public void deleteFavorite(Long id) {
        log.info("Deleting favorite with ID: {}", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Unauthorized user tried to delete favorite ID: {}", id);
            throw new AccessDeniedException("User is not authenticated");
        }

        Favorites favorites = favoritesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorite not found with ID: {}", id);
                    return new EntityNotFoundException("Favorites with id " + id + " not found", HttpStatus.NOT_FOUND);
                });

        if (!favorites.getUser().getId().equals(user.getId())) {
            log.warn("User ID {} tried to delete favorite owned by user ID {}", user.getId(), favorites.getUser().getId());
            throw new AccessDeniedException("You are not allowed to delete this resource");
        }

        favoritesRepository.deleteById(favorites.getId());
        log.info("Deleted favorite ID {} for user ID {}", favorites.getId(), user.getId());
    }
}
