package dev.domaincentric.sample.ecommerce.pricing.domain.event;

import dev.domaincentric.sample.ecommerce.pricing.domain.model.PriceId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a product's price was changed. */
public record PriceChanged(
    UUID eventId,
    PriceId priceId,
    ProductId productId,
    Money oldPrice,
    Money newPrice,
    Instant effectiveFrom,
    Instant occurredOn)
    implements DomainEvent {

  public static PriceChanged now(
      final PriceId priceId,
      final ProductId productId,
      final Money oldPrice,
      final Money newPrice,
      final Instant effectiveFrom) {
    return new PriceChanged(
        UUID.randomUUID(), priceId, productId, oldPrice, newPrice, effectiveFrom, Instant.now());
  }
}
