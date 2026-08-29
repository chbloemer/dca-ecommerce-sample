package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that stock was decreased for a product. */
public record StockDecreased(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity removedQuantity,
    StockQuantity newQuantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockDecreased now(
      final StockLevelId stockLevelId,
      final ProductId productId,
      final StockQuantity removedQuantity,
      final StockQuantity newQuantity) {
    return new StockDecreased(
        UUID.randomUUID(), stockLevelId, productId, removedQuantity, newQuantity, Instant.now());
  }
}
