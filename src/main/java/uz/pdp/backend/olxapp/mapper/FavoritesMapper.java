package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.backend.olxapp.entity.Favorites;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;


@Mapper(componentModel = "spring")
public interface FavoritesMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userId", source = "user.id")
    FavoritesDTO toDto(Favorites entity);

    Favorites toEntity(Favorites dto);

}
