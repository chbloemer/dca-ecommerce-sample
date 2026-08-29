package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutStep;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that a checkout session expired due to inactivity.
 *
 * <p>This event is raised when a checkout session times out without being completed or explicitly
 * abandoned.
 */
public record CheckoutExpired(
    UUID eventId, CheckoutSessionId sessionId, CheckoutStep expiredAtStep, Instant occurredOn)
    implements DomainEvent {

  public static CheckoutExpired now(
      final CheckoutSessionId sessionId, final CheckoutStep expiredAtStep) {
    return new CheckoutExpired(UUID.randomUUID(), sessionId, expiredAtStep, Instant.now());
  }
}
