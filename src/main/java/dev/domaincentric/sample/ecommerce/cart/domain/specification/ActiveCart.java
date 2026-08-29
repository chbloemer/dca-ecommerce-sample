package dev.domaincentric.sample.ecommerce.cart.domain.specification;

import dev.domaincentric.sample.ecommerce.cart.domain.model.CartStatus;
import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.AndSpecification;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.SpecificationVisitor;

/** Cart is in ACTIVE status. */
public record ActiveCart() implements CartSpecification {
  @Override
  public boolean isSatisfiedBy(ShoppingCart candidate) {
    return candidate.status() == CartStatus.ACTIVE;
  }

  @Override
  public <R> R accept(SpecificationVisitor<ShoppingCart, R> visitor) {
    if (visitor instanceof CartSpecificationVisitor<?> v) {
      @SuppressWarnings("unchecked")
      final CartSpecificationVisitor<R> cv = (CartSpecificationVisitor<R>) v;
      return cv.visit(this);
    }
    return visitor.visit(new AndSpecification<>(this, this));
  }
}
