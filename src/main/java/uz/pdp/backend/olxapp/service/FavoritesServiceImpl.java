package uz.pdp.backend.olxapp.service;

import lombok.AllArgsConstructor;
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

@Service
@AllArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final FavoritesMapper favoritesMapper;
    private final ProductRepository productRepository;

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

    @Override
    public void addFavorite(FavoriteReqDTO favoriteReqDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof User user)) {
            throw new AccessDeniedException("User is not authenticated");
        }

        Product product = productRepository.findById(favoriteReqDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + favoriteReqDTO.getProductId() + " not found", HttpStatus.NOT_FOUND));

        Favorites favorites = new Favorites(
                user,
                product
        );
        favoritesRepository.save(favorites);

    }

    @Override
    public void deleteFavorite(Long id) {

        Favorites favorites = favoritesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Favorites with id " + id + " not found", HttpStatus.NOT_FOUND));

        favoritesRepository.delete(favorites);

    }
}
