package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class})
public interface ProductMapper {
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "productImages", target = "productImages")
    ProductDTO toDto(Product product);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "createdById", target = "createdBy.id")
    Product toEntity(ProductDTO dto);
}

