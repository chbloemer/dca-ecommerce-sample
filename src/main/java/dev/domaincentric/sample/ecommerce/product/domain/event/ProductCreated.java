package dev.domaincentric.sample.ecommerce.product.domain.event;

import dev.domaincentric.sample.ecommerce.product.domain.model.ProductName;
import dev.domaincentric.sample.ecommerce.product.domain.model.SKU;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain Event indicating that a new product was created.
 *
 * <p>This event includes:
 *
 * <ul>
 *   <li>Initial price - for the Pricing bounded context to initialize ProductPrice entity
 *   <li>Initial stock - for the Inventory bounded context to initialize StockLevel entity
 * </ul>
 *
 * <p>While pricing is managed by the Pricing context and stock by the Inventory context, these
 * initial values are captured at product creation time to ensure data is available immediately in
 * the respective contexts.
 */
public record ProductCreated(
    UUID eventId,
    ProductId productId,
    SKU sku,
    ProductName name,
    Money initialPrice,
    int initialStock,
    Instant occurredOn)
    implements DomainEvent {

  public static ProductCreated now(
      final ProductId productId,
      final SKU sku,
      final ProductName name,
      final Money initialPrice,
      final int initialStock) {
    return new ProductCreated(
        UUID.randomUUID(), productId, sku, name, initialPrice, initialStock, Instant.now());
  }
}
