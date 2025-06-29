package uz.pdp.backend.olxapp.payload;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Avazbek on 29/06/25 23:48
 */
@Data
public class ProductFilterDTO {

    private Optional<String> searchText = Optional.empty();

    // Для фильтрации по категории
    private Optional<Long> categoryId = Optional.empty();

    // Для фильтрации по цене "от" и "до"
    private Optional<BigDecimal> minPrice = Optional.empty();
    private Optional<BigDecimal> maxPrice = Optional.empty();


}
