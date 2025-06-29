package uz.pdp.backend.olxapp.specifation;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.enums.Status;
import uz.pdp.backend.olxapp.payload.ProductFilterDTO;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Avazbek on 29/06/25 23:48
 */
public class ProductSpecification {

    public static Specification<Product> filterBy(ProductFilterDTO filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), Status.ACTIVE));

            filter.getSearchText().ifPresent(text -> {
                String pattern = "%" + text.toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), pattern);
                Predicate descriptionLike = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(titleLike, descriptionLike));
            });

            filter.getCategoryId().ifPresent(categoryId -> predicates.add(cb.equal(root.join("category").get("id"), categoryId)));

            filter.getMinPrice().ifPresent(minPrice -> predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice)));

            filter.getMaxPrice().ifPresent(maxPrice -> predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }

}
