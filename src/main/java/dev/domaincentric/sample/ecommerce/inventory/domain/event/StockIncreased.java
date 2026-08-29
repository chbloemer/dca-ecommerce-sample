package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that stock was increased for a product. */
public record StockIncreased(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity addedQuantity,
    StockQuantity newQuantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockIncreased now(
      final StockLevelId stockLevelId,
      final ProductId productId,
      final StockQuantity addedQuantity,
      final StockQuantity newQuantity) {
    return new StockIncreased(
        UUID.randomUUID(), stockLevelId, productId, addedQuantity, newQuantity, Instant.now());
  }
}
