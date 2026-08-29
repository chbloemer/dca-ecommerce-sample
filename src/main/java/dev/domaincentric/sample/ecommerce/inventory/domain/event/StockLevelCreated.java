package dev.domaincentric.sample.ecommerce.inventory.domain.event;

import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockQuantity;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a new stock level was created for a product. */
public record StockLevelCreated(
    UUID eventId,
    StockLevelId stockLevelId,
    ProductId productId,
    StockQuantity quantity,
    Instant occurredOn)
    implements DomainEvent {

  public static StockLevelCreated now(
      final StockLevelId stockLevelId, final ProductId productId, final StockQuantity quantity) {
    return new StockLevelCreated(
        UUID.randomUUID(), stockLevelId, productId, quantity, Instant.now());
  }
}
