package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that previously reserved stock was released. */
public record StockReleased(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity releasedQuantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockReleased now(
      final StockLevelId stockLevelId,
      final ProductId productId,
      final StockQuantity releasedQuantity) {
    return new StockReleased(
        UUID.randomUUID(), stockLevelId, productId, releasedQuantity, Instant.now());
  }
}
