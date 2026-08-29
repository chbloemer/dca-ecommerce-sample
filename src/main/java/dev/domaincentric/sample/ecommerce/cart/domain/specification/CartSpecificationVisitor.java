package dev.domaincentric.sample.ecommerce.cart.domain.specification;

import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.SpecificationVisitor;

/**
 * Visitor for translating cart specifications to adapter-specific forms.
 *
 * <p>Adapters (e.g., JPA) implement this interface to translate a domain-level cart specification
 * into a query predicate or similar construct.
 */
public interface CartSpecificationVisitor<R> extends SpecificationVisitor<ShoppingCart, R> {
  R visit(ActiveCart spec);

  R visit(LastUpdatedBefore spec);

  R visit(HasMinTotal spec);

  R visit(HasAnyAvailableItem spec);

  R visit(CustomerAllowsMarketing spec);
}
