package dev.domaincentric.sample.ecommerce.product.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.product.domain.model.ProductDescription;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a product's description was changed. */
public record ProductDescriptionChanged(
    UUID eventId,
    ProductId productId,
    ProductDescription oldDescription,
    ProductDescription newDescription,
    Instant occurredOn)
    implements DomainEvent {

  public static ProductDescriptionChanged now(
      final ProductId productId,
      final ProductDescription oldDescription,
      final ProductDescription newDescription) {
    return new ProductDescriptionChanged(
        UUID.randomUUID(), productId, oldDescription, newDescription, Instant.now());
  }
}
