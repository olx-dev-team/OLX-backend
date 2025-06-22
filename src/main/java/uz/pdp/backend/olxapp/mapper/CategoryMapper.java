package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.payload.CategoryDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDto(Category entity);

    Category toEntity(CategoryDTO dto);
}
