package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uz.pdp.backend.olxapp.entity.ProductImage;
import uz.pdp.backend.olxapp.entity.ProductImageDto;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "attachment.id", target = "attachmentId")
    @Mapping(source = "main", target = "main") // ✅ entity → DTO
    ProductImageDto toDto(ProductImage image);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "attachmentId", target = "attachment.id")
    @Mapping(source = "main", target = "main") // ✅ DTO → entity
    ProductImage toEntity(ProductImageDto dto);
}


