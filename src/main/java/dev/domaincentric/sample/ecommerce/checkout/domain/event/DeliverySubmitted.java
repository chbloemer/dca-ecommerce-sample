package dev.domaincentric.sample.ecommerce.checkout.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.DeliveryAddress;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.ShippingOption;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that delivery details were submitted for a checkout session.
 *
 * <p>This event is raised when a customer completes the delivery step, providing their shipping
 * address and selecting a shipping option.
 */
public record DeliverySubmitted(
    UUID eventId,
    CheckoutSessionId sessionId,
    String deliveryAddress,
    String shippingOptionId,
    Money shippingCost,
    Instant occurredOn)
    implements DomainEvent {

  public static DeliverySubmitted now(
      final CheckoutSessionId sessionId,
      final DeliveryAddress address,
      final ShippingOption shippingOption) {
    return new DeliverySubmitted(
        UUID.randomUUID(),
        sessionId,
        address.formattedAddress(),
        shippingOption.id(),
        shippingOption.cost(),
        Instant.now());
  }
}
