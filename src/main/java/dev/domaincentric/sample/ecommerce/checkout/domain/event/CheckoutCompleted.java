package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

/**
 * Domain Event indicating that a checkout was successfully completed.
 *
 * <p>This event is raised when payment has been processed and the order is finalized.
 */
public record CheckoutCompleted(
    UUID eventId,
    CheckoutSessionId sessionId,
    Money totalAmount,
    @Nullable String orderReference,
    Instant occurredOn)
    implements DomainEvent {

  public static CheckoutCompleted now(
      final CheckoutSessionId sessionId,
      final Money totalAmount,
      @Nullable final String orderReference) {
    return new CheckoutCompleted(
        UUID.randomUUID(), sessionId, totalAmount, orderReference, Instant.now());
  }
}
