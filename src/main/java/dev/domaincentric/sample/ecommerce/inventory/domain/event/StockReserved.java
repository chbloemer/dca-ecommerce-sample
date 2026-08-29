package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that stock was reserved for a product. */
public record StockReserved(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity reservedQuantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockReserved now(
      final StockLevelId stockLevelId,
      final ProductId productId,
      final StockQuantity reservedQuantity) {
    return new StockReserved(
        UUID.randomUUID(), stockLevelId, productId, reservedQuantity, Instant.now());
  }
}
