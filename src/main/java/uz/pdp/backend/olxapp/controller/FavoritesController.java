package uz.pdp.backend.olxapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Favorites", description = "Endpoints for managing user favorite items")
public class FavoritesController {

    private final FavoritesService favoritesService;

    @Operation(
            summary = "Get all favorites (paginated)",
            description = "Retrieves a paginated list of all favorite items for the authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of favorite items retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public PageDTO<FavoritesDTO> getFavorites(
            @RequestParam(defaultValue = "0") @Schema(description = "Page number", example = "0") Integer page,
            @RequestParam(defaultValue = "10") @Schema(description = "Page size", example = "10") Integer size) {
        return favoritesService.getAllFavorites(page, size);
    }

    @Operation(
            summary = "Get a specific favorite by ID",
            description = "Returns the details of a favorite item by its unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorite found"),
                    @ApiResponse(responseCode = "404", description = "Favorite not found")
            }
    )
    @GetMapping("/{id}")
    public FavoritesDTO getFavorites(
            @PathVariable @Schema(description = "ID of the favorite item", example = "5") Long id) {
        return favoritesService.getByIdFavorites(id);
    }

    @Operation(
            summary = "Add or remove favorite",
            description = "Adds a product to the user's favorites. If the product is already favorited, it removes it.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteReqDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Toggle Favorite",
                                            summary = "Add product to favorites",
                                            value = "{ \"productId\": 1, \"userId\": 42 }"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Favorite toggled successfully")
            }
    )
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteReqDTO favoriteReqDTO) {
        String result = favoritesService.addFavorite(favoriteReqDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "Delete favorite by ID",
            description = "Deletes a favorite item based on its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorite deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Favorite not found")
            }
    )
    @DeleteMapping("/{id}")
    public void deleteFavorite(
            @PathVariable @Schema(description = "ID of the favorite to delete", example = "3") Long id) {
        favoritesService.deleteFavorite(id);
    }
}