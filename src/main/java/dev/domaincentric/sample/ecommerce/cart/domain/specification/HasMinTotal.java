package dev.domaincentric.sample.ecommerce.cart.domain.specification;

import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.AndSpecification;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.SpecificationVisitor;
import java.util.Objects;

/** Cart total (sum of item price * quantity) is greater than or equal to the given minimum. */
public record HasMinTotal(Money minimum) implements CartSpecification {
  public HasMinTotal {
    Objects.requireNonNull(minimum, "minimum must not be null");
  }

  @Override
  public boolean isSatisfiedBy(ShoppingCart candidate) {
    final Money total = candidate.calculateTotal();
    if (!total.currency().equals(minimum.currency())) {
      return false; // different currencies cannot be compared here
    }
    return total.amount().compareTo(minimum.amount()) >= 0;
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
