package dev.domaincentric.sample.ecommerce.product.domain.event;

import dev.domaincentric.sample.ecommerce.product.domain.model.Category;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a product's category was changed. */
public record ProductCategoryChanged(
    UUID eventId,
    ProductId productId,
    Category oldCategory,
    Category newCategory,
    Instant occurredOn)
    implements DomainEvent {

  public static ProductCategoryChanged now(
      final ProductId productId, final Category oldCategory, final Category newCategory) {
    return new ProductCategoryChanged(
        UUID.randomUUID(), productId, oldCategory, newCategory, Instant.now());
  }
}
