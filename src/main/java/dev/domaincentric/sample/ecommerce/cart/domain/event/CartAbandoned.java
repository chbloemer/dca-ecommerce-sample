package dev.domaincentric.sample.ecommerce.cart.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a shopping cart was abandoned by the customer. */
public record CartAbandoned(UUID eventId, CartId cartId, Instant occurredOn)
    implements DomainEvent {

  public static CartAbandoned now(final CartId cartId) {
    return new CartAbandoned(UUID.randomUUID(), cartId, Instant.now());
  }
}
