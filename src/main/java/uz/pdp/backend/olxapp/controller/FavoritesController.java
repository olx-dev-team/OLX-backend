package uz.pdp.backend.olxapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.backend.olxapp.payload.FavoriteReqDTO;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/close/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Operations related to managing favorite products")
public class FavoritesController {

    private final FavoritesService favoritesService;

    /**
     * Test successfully!
     *
     * @param page page number default is 0
     * @param size page size default is 10
     * @return page dto of favorites
     */
    @Operation(
            summary = "Get all favorites of current user",
            description = "Returns paginated list of favorite products of the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved favorites")
            }
    )
    @GetMapping
    public PageDTO<FavoritesDTO> getFavorites(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return favoritesService.getAllFavorites(page, size);
    }

    /**
     * Test successfully!
     *
     * @param id - with favorite id parameter
     * @return favorites dto by given id
     */
    @Operation(
            summary = "Get favorite by ID",
            description = "Returns a specific favorite by its ID. Only accessible by its owner.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorite found"),
                    @ApiResponse(responseCode = "404", description = "Favorite not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @GetMapping("/{id}")
    public FavoritesDTO getFavorites(@PathVariable Long id) {
        return favoritesService.getByIdFavorites(id);
    }


    /**
     * Test successfully!
     *
     * @param favoriteReqDTO - request body for adding new favorite
     */
    @Operation(
            summary = "Add or remove a product from favorites",
            description = """
                    If the product is not yet in favorites, it will be added.
                    If it is already favorited, it will be removed.
                    """,
            requestBody = @RequestBody(
                    description = "DTO containing the productId to add/remove from favorites",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteReqDTO.class),
                            examples = @ExampleObject(value = """
                            {
                              "productId": 1
                            }
                            """)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Added or removed from favorites"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "403", description = "User not authenticated")
            }
    )
    @PostMapping
    public ResponseEntity<?> addFavorite(@Valid @org.springframework.web.bind.annotation.RequestBody FavoriteReqDTO favoriteReqDTO) {
        favoritesService.addFavorite(favoriteReqDTO);
        return ResponseEntity.accepted().build();
    }

    /**
     * Test successfully!
     *
     * @param id - with favorite id parameter
     */
    @Operation(
            summary = "Delete favorite by ID",
            description = "Deletes a favorite record by its ID. Only accessible by its owner.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Favorite not found"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @DeleteMapping("/{id}")
    public void deleteFavorite(@PathVariable Long id) {
        favoritesService.deleteFavorite(id);
    }
}
