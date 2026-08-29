package dev.domaincentric.sample.ecommerce.product.domain.event;

import dev.domaincentric.sample.ecommerce.product.domain.model.ProductName;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a product's name was changed. */
public record ProductNameChanged(
    UUID eventId, ProductId productId, ProductName oldName, ProductName newName, Instant occurredOn)
    implements DomainEvent {

  public static ProductNameChanged now(
      final ProductId productId, final ProductName oldName, final ProductName newName) {
    return new ProductNameChanged(UUID.randomUUID(), productId, oldName, newName, Instant.now());
  }
}
