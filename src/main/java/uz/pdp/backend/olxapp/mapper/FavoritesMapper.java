package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import uz.pdp.backend.olxapp.entity.Favorites;
import uz.pdp.backend.olxapp.payload.FavoritesDTO;


@Mapper(componentModel = "spring")
public interface FavoritesMapper {

    FavoritesDTO toDto(Favorites entity);

    Favorites toEntity(Favorites dto);

}
