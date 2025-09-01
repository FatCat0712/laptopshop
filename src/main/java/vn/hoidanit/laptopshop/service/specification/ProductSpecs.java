package vn.hoidanit.laptopshop.service.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;

import java.util.List;

public class ProductSpecs {
    public static Specification<Product> nameLike(String name) {
        return (root, query, builder) -> builder.like(root.get(Product_.NAME), "%" + name + "%");
    }

    public static Specification<Product> minPrice(double minPrice) {
        return (root, query, builder) -> builder.ge(root.get(Product_.PRICE), minPrice);
    }

    public static Specification<Product> maxPrice(double maxPrice) {
        return (root, query, builder) -> builder.le(root.get(Product_.PRICE), maxPrice);
    }

    public static Specification<Product> matchListFactory(List<String> factories) {
        return (root, query, builder) -> builder.in(root.get(Product_.FACTORY)).value(factories);
    }

    public static Specification<Product> matchListTarget(List<String> targets) {
        return ((root, query, builder) -> builder.in(root.get(Product_.TARGET)).value(targets));
    }

    public static Specification<Product> matchListPrice(double min, double max) {
        return (root, query, builder) -> builder.between(root.get(Product_.PRICE), min, max);
    }




}
