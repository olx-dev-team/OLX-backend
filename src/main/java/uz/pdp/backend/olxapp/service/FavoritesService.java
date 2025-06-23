package uz.pdp.backend.olxapp.service;

import uz.pdp.backend.olxapp.payload.FavoriteReqDTO;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;

public interface FavoritesService {
    PageDTO<FavoritesDTO> getAllFavorites(Integer page, Integer size);

    FavoritesDTO getByIdFavorites(Long id);

    String addFavorite(FavoriteReqDTO favoriteReqDTO);

    void deleteFavorite(Long id);
}
