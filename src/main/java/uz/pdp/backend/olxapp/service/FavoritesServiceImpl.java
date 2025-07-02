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
import uz.pdp.backend.olxapp.enums.Status;
import uz.pdp.backend.olxapp.exception.EntityNotFoundException;
import uz.pdp.backend.olxapp.mapper.FavoritesMapper;
import uz.pdp.backend.olxapp.payload.FavoriteReqDTO;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.repository.FavoritesRepository;
import uz.pdp.backend.olxapp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final FavoritesMapper favoritesMapper;
    private final ProductRepository productRepository;

    /// tekshirildi
    @Override
    public PageDTO<FavoritesDTO> getAllFavorites(Integer page, Integer size) {

        Sort sort = Sort.by(AbstractEntity.Fields.createdAt);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Page<Favorites> favoritesPage = favoritesRepository.findByUser(user, pageRequest);

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

    ///  teskshirildi
    @Override
    public FavoritesDTO getByIdFavorites(Long id) {

        Favorites favorites = favoritesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Favorites with id " + id + " not found", HttpStatus.NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        if (!favorites.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        return favoritesMapper.toDto(favorites);

    }

    ///  tekshirildi
    @Override
    public void addFavorite(FavoriteReqDTO favoriteReqDTO) {
        log.info("User attempting to add/remove favorite for productId={}", favoriteReqDTO.getProductId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Access denied: user not authenticated");
            throw new AccessDeniedException("User is not authenticated");
        }

        Product product = productRepository.findByIdAndStatus(favoriteReqDTO.getProductId(), List.of(Status.ACTIVE))
                .orElseThrow(() -> {
                    log.warn("Active product not found: {}", favoriteReqDTO.getProductId());
                    return new EntityNotFoundException("Product with id " + favoriteReqDTO.getProductId() + " not found", HttpStatus.NOT_FOUND);
                });

        Optional<Favorites> existingFavorite = favoritesRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (existingFavorite.isPresent()) {
            favoritesRepository.delete(existingFavorite.get());
            log.info("Favorite removed: userId={}, productId={}", user.getId(), product.getId());
        } else {
            Favorites favorite = new Favorites(user, product);
            favoritesRepository.save(favorite);
            log.info("Favorite added: userId={}, productId={}", user.getId(), product.getId());
        }
    }

    @Override
    public void deleteFavorite(Long id) {
        log.info("Deleting favorite with id {}", id);

        Favorites favorites = favoritesRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Favorite not found: {}", id);
                    return new EntityNotFoundException("Favorites with id " + id + " not found", HttpStatus.NOT_FOUND);
                });

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            log.warn("Access denied: user not authenticated");
            throw new AccessDeniedException("User is not authenticated");
        }

        if (!favorites.getUser().getId().equals(user.getId())) {
            log.warn("User {} tried to delete another user's favorite (favoriteId={})", user.getId(), id);
            throw new AccessDeniedException("You are not allowed to access this resource");
        }

        favoritesRepository.delete(favorites);
        log.info("Favorite deleted successfully: favoriteId={}, userId={}", id, user.getId());
    }
}
