package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.PaymentSelection;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that payment method was submitted for a checkout session.
 *
 * <p>This event is raised when a customer completes the payment step, selecting their preferred
 * payment method.
 */
public record PaymentSubmitted(
    UUID eventId, CheckoutSessionId sessionId, String paymentProviderId, Instant occurredOn)
    implements DomainEvent {

  public static PaymentSubmitted now(
      final CheckoutSessionId sessionId, final PaymentSelection payment) {
    return new PaymentSubmitted(
        UUID.randomUUID(), sessionId, payment.providerId().value(), Instant.now());
  }
}
