package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutStep;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that a checkout session was abandoned by the customer.
 *
 * <p>This event is raised when a customer explicitly cancels their checkout or navigates away from
 * the checkout flow.
 */
public record CheckoutAbandoned(
    UUID eventId, CheckoutSessionId sessionId, CheckoutStep abandonedAtStep, Instant occurredOn)
    implements DomainEvent {

  public static CheckoutAbandoned now(
      final CheckoutSessionId sessionId, final CheckoutStep abandonedAtStep) {
    return new CheckoutAbandoned(UUID.randomUUID(), sessionId, abandonedAtStep, Instant.now());
  }
}
