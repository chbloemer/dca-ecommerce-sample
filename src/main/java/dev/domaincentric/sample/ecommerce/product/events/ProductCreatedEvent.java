package dev.domaincentric.sample.ecommerce.product.events;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.IntegrationEvent;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.IntegrationEventType;
import java.time.Instant;
import java.util.UUID;

/**
 * Integration Event published when a new product is created.
 *
 * <p>This event is published for cross-module consumption. Internal domain event {@code
 * ProductCreated} is converted to this integration event by {@code ProductCreatedEventPublisher}.
 *
 * <p>Consumers: Pricing context (creates initial price), Inventory context (creates stock level).
 */
@IntegrationEventType(name = "product-created", version = 1)
public record ProductCreatedEvent(
    UUID eventId, ProductId productId, Money initialPrice, int initialStock, Instant occurredOn)
    implements IntegrationEvent {

  /** Creates a new event from product creation data. */
  public static ProductCreatedEvent now(ProductId productId, Money initialPrice, int initialStock) {
    return new ProductCreatedEvent(
        UUID.randomUUID(), productId, initialPrice, initialStock, Instant.now());
  }
}
