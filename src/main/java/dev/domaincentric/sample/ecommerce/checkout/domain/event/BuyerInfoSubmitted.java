package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.BuyerInfo;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that buyer information was submitted for a checkout session.
 *
 * <p>This event is raised when a customer completes the buyer info step, providing their contact
 * details.
 */
public record BuyerInfoSubmitted(
    UUID eventId,
    CheckoutSessionId sessionId,
    String email,
    String firstName,
    String lastName,
    Instant occurredOn)
    implements DomainEvent {

  public static BuyerInfoSubmitted now(
      final CheckoutSessionId sessionId, final BuyerInfo buyerInfo) {
    return new BuyerInfoSubmitted(
        UUID.randomUUID(),
        sessionId,
        buyerInfo.email(),
        buyerInfo.firstName(),
        buyerInfo.lastName(),
        Instant.now());
  }
}
