package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.payload.CategoryDTO;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryDTO toDto(Category entity);

    Category toEntity(CategoryDTO dto);
}
