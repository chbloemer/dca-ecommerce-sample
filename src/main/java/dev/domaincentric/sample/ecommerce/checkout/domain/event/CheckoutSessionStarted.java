package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.sample.ecommerce.checkout.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CustomerId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that a checkout session was started.
 *
 * <p>This event is raised when a customer begins the checkout process from their shopping cart.
 */
public record CheckoutSessionStarted(
    UUID eventId,
    CheckoutSessionId sessionId,
    CartId cartId,
    CustomerId customerId,
    Money subtotal,
    int lineItemCount,
    Instant occurredOn)
    implements DomainEvent {

  public static CheckoutSessionStarted now(
      final CheckoutSessionId sessionId,
      final CartId cartId,
      final CustomerId customerId,
      final Money subtotal,
      final int lineItemCount) {
    return new CheckoutSessionStarted(
        UUID.randomUUID(), sessionId, cartId, customerId, subtotal, lineItemCount, Instant.now());
  }
}
