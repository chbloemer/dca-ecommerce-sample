package dev.domaincentric.sample.ecommerce.cart.domain.event;

import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that a product was removed from a shopping cart.
 *
 * <p>This event is raised when a customer removes an entire product (all quantity) from their
 * shopping cart.
 */
public record ProductRemovedFromCart(
    UUID eventId, CartId cartId, ProductId productId, Instant occurredOn) implements DomainEvent {

  public static ProductRemovedFromCart now(final CartId cartId, final ProductId productId) {
    return new ProductRemovedFromCart(UUID.randomUUID(), cartId, productId, Instant.now());
  }
}
