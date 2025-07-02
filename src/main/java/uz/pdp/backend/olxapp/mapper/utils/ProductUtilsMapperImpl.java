package uz.pdp.backend.olxapp.mapper.utils;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Component;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.ProductImage;
import uz.pdp.backend.olxapp.payload.ProductModerationListDTO;
import uz.pdp.backend.olxapp.payload.ProductModerationStatusDTO;

@Component
public class ProductUtilsMapperImpl implements ProductUtilsMapper {

    @Override
    public ProductModerationListDTO mapToRejectedProductListItemDTO(Product product) {
        return ProductModerationListDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .rejectionReasons(product.getRejectionReasons())
                .mainImageUrl(getMainImageUrl(product))
                .build();
    }

    // Вспомогательный метод для получения URL главной картинки. Тоже не меняется.
    public String getMainImageUrl(Product product) {
        if (product.getProductImages() == null || product.getProductImages().isEmpty()) {
            return null; // или URL картинки-заглушки
        }
        ProductImage mainImage = product.getProductImages().get(0);
        return "/api/attachments/" + mainImage.getId(); // Пример URL
    }

    @Override
    public ProductModerationStatusDTO mapToModerationStatusDTO(Product product) {
        return ProductModerationStatusDTO.builder()
                .productId(product.getId())
                .status(product.getStatus())
                .rejectionReasons(product.getRejectionReasons())
                .build();
    }

}
