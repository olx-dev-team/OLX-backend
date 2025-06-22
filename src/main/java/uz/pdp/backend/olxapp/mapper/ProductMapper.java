package uz.pdp.backend.olxapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import uz.pdp.backend.olxapp.entity.Attachment;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.payload.AttachmentDTO;
import uz.pdp.backend.olxapp.payload.ProductDTO;
import uz.pdp.backend.olxapp.payload.ProductUpdateDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    /**
     * ProductUpdateDTO'dagi ma'lumotlarni mavjud Product entity'siga yangilaydi.
     * @MappingTarget annotatsiyasi product obyektini o'zgartirish kerakligini bildiradi.
     * NullValuePropertyMappingStrategy.IGNORE - DTO'da null bo'lgan maydonlarni e'tiborsiz qoldiradi.
     * Masalan, agar DTO'da price null kelsa, Product'dagi eski narxni o'zgartirmaydi.
     */
    void updateFromDto(ProductUpdateDTO dto, @MappingTarget Product product);
    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);

}
