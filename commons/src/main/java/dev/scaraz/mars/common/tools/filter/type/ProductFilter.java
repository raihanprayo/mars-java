package dev.scaraz.mars.common.tools.filter.type;

import dev.scaraz.mars.common.tools.enums.Product;

import java.util.Collection;

public class ProductFilter extends EnumFilter<Product> {
    @Override
    public ProductFilter setGt(Product greaterThan) {
        return (ProductFilter) super.setGt(greaterThan);
    }

    @Override
    public ProductFilter setGte(Product greaterThanEqual) {
        return (ProductFilter) super.setGte(greaterThanEqual);
    }

    @Override
    public ProductFilter setLt(Product lessThan) {
        return (ProductFilter) super.setLt(lessThan);
    }

    @Override
    public ProductFilter setLte(Product lessThanEqual) {
        return (ProductFilter) super.setLte(lessThanEqual);
    }

    @Override
    public ProductFilter setEq(Product value) {
        return (ProductFilter) super.setEq(value);
    }

    @Override
    public ProductFilter setIn(Collection<Product> in) {
        return (ProductFilter) super.setIn(in);
    }

    @Override
    public ProductFilter setSpecified(Boolean nullable) {
        return (ProductFilter) super.setSpecified(nullable);
    }

    @Override
    public ProductFilter setNegated(boolean negated) {
        return (ProductFilter) super.setNegated(negated);
    }
}
