package uz.pdp.backend.olxapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.FavoriteReqDTO;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.FavoritesService;

@RestController
@RequestMapping("/api/close/v1/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    /**
     * Test successfully!
     *
     * @param page page number default is 0
     * @param size page size default is 10
     * @return page dto of favorites
     */
    @GetMapping
    public PageDTO<FavoritesDTO> getFavorites(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {


        PageDTO<FavoritesDTO> allFavorites = favoritesService.getAllFavorites(page, size);
        return allFavorites;

    }

    /**
     * Test successfully!
     *
     * @param id - with favorite id parameter
     * @return favorites dto by given id
     */
    @GetMapping("/{id}")
    public FavoritesDTO getFavorites(@PathVariable Long id) {

        return favoritesService.getByIdFavorites(id);

    }

    /**
     * Test successfully!
     *
     * @param favoriteReqDTO - request body for adding new favorite
     */
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteReqDTO favoriteReqDTO) {

        String string = favoritesService.addFavorite(favoriteReqDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(string);
    }

    /**
     * Test successfully!
     *
     * @param id - with favorite id parameter
     */
    @DeleteMapping("/{id}")
    public void deleteFavorite(@PathVariable Long id) {
        favoritesService.deleteFavorite(id);
    }


}
