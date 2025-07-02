package uz.pdp.backend.olxapp.mapper.utils;

import org.springframework.data.domain.Page;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.payload.PageDTO;
import uz.pdp.backend.olxapp.payload.ProductModerationListDTO;
import uz.pdp.backend.olxapp.payload.ProductModerationStatusDTO;

public interface ProductUtilsMapper {
    ProductModerationStatusDTO mapToModerationStatusDTO(Product product);

    ProductModerationListDTO mapToRejectedProductListItemDTO(Product product);

}
