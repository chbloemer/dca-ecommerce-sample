package dev.domaincentric.sample.ecommerce.cart.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.cart.domain.model.Quantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an item was added to a shopping cart. */
public record CartItemAddedToCart(
    UUID eventId, CartId cartId, ProductId productId, Quantity quantity, Instant occurredOn)
    implements DomainEvent {

  public static CartItemAddedToCart now(
      final CartId cartId, final ProductId productId, final Quantity quantity) {
    return new CartItemAddedToCart(UUID.randomUUID(), cartId, productId, quantity, Instant.now());
  }
}
