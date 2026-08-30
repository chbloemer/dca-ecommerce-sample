package dev.domaincentric.sample.ecommerce.cart.domain.specification;

import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.CompositeSpecification;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.SpecificationVisitor;
import java.util.Objects;

/**
 * Wraps a {@code CompositeSpecification<ShoppingCart>} as a {@link CartSpecification}; repositories
 * accept the composite directly, so this wrapper is only needed where a {@code CartSpecification}
 * type is required.
 */
public final class ComposedCartSpecification implements CompositeSpecification<ShoppingCart> {

  private final CompositeSpecification<ShoppingCart> delegate;

  public ComposedCartSpecification(final CompositeSpecification<ShoppingCart> delegate) {
    this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
  }

  @Override
  public boolean isSatisfiedBy(final ShoppingCart candidate) {
    return delegate.isSatisfiedBy(candidate);
  }

  @Override
  public <R> R accept(final SpecificationVisitor<ShoppingCart, R> visitor) {
    return delegate.accept(visitor);
  }
}
