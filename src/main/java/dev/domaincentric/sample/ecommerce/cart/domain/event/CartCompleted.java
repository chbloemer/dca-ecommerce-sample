package dev.domaincentric.sample.ecommerce.cart.domain.event;

import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a shopping cart was completed after checkout confirmation. */
public record CartCompleted(UUID eventId, CartId cartId, Instant occurredOn)
    implements DomainEvent {

  public static CartCompleted now(final CartId cartId) {
    return new CartCompleted(UUID.randomUUID(), cartId, Instant.now());
  }
}
