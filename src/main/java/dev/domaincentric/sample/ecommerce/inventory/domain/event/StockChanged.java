package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that stock levels have changed for a product. */
public record StockChanged(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity previousAvailableQuantity,
    StockQuantity newAvailableQuantity,
    StockQuantity previousReservedQuantity,
    StockQuantity newReservedQuantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockChanged now(
      final StockLevelId stockLevelId,
      final ProductId productId,
      final StockQuantity previousAvailableQuantity,
      final StockQuantity newAvailableQuantity,
      final StockQuantity previousReservedQuantity,
      final StockQuantity newReservedQuantity) {
    return new StockChanged(
        UUID.randomUUID(),
        stockLevelId,
        productId,
        previousAvailableQuantity,
        newAvailableQuantity,
        previousReservedQuantity,
        newReservedQuantity,
        Instant.now());
  }
}
