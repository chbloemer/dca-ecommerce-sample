package dev.domaincentric.sample.ecommerce.cart.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that all items were removed from a shopping cart.
 *
 * <p>This event is raised when a customer clears their entire shopping cart, removing all items at
 * once.
 */
public record CartCleared(UUID eventId, CartId cartId, int itemsCleared, Instant occurredOn)
    implements DomainEvent {

  public static CartCleared now(final CartId cartId, final int itemsCleared) {
    return new CartCleared(UUID.randomUUID(), cartId, itemsCleared, Instant.now());
  }
}
