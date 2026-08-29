package dev.domaincentric.sample.ecommerce.cart.domain.specification;

import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.specification.CompositeSpecification;

/**
 * Marker interface for cart-related specifications.
 *
 * <p>Extends the generic {@link CompositeSpecification} for {@link ShoppingCart} so that adapters
 * can translate the specific leaf specifications without leaking JPA into the domain.
 */
public sealed interface CartSpecification extends CompositeSpecification<ShoppingCart>
    permits ActiveCart,
        LastUpdatedBefore,
        HasMinTotal,
        HasAnyAvailableItem,
        CustomerAllowsMarketing {}
