package uz.pdp.backend.olxapp.service;

import org.springframework.data.jpa.domain.Specification;
import uz.pdp.backend.olxapp.entity.Category;
import uz.pdp.backend.olxapp.entity.Product;
import uz.pdp.backend.olxapp.entity.User;
import uz.pdp.backend.olxapp.payload.FilterDTO;

public class ProductSpecifications {
    public static Specification<Product> build(FilterDTO filter) {

        if (filter == null) return Specification.anyOf();

        Specification<Product> spec = Specification.anyOf();

        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            spec = spec.and(byGeneralSearch(filter.getSearch()));
        }
        if (filter.getTitle() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
        }
        if (filter.getDescription() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
        }
        if (filter.getCategoryName() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category").get("name")), filter.getCategoryName().toLowerCase()));
        }
        if (filter.getFromPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), filter.getFromPrice()));
        }
        if (filter.getToPrice() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), filter.getToPrice()));
        }
        if (filter.getFromCreatedAt() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFromCreatedAt()));
        }
        if (filter.getToCreatedAt() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), filter.getToCreatedAt()));
        }
        if (filter.getFromUpdatedAt() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("updatedAt"), filter.getFromUpdatedAt()));
        }
        if (filter.getToUpdatedAt() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("updatedAt"), filter.getToUpdatedAt()));
        }

        return spec;
    }

    private static Specification<Product> byGeneralSearch(String search) {
        String pattern = "%" + search.toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get(Product.Fields.title)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.description)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.category).get(Category.Fields.name)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.createdBy).get(User.Fields.firstName)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.createdBy).get(User.Fields.lastName)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.createdBy).get(User.Fields.username)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.createdBy).get(User.Fields.email)), pattern),
                cb.like(cb.lower(root.get(Product.Fields.createdBy).get(User.Fields.phoneNumber)), pattern)
        );
    }

}
