package dev.domaincentric.sample.ecommerce.cart.events;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.IntegrationEvent;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.IntegrationEventType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Integration Event published when a shopping cart is checked out.
 *
 * <p>This event is published for cross-module consumption. Internal domain event {@code
 * CartCheckedOut} is converted to this integration event by {@code CartCheckedOutEventPublisher}.
 */
@IntegrationEventType(name = "cart-checked-out", version = 1)
public record CartCheckedOutEvent(
    UUID eventId,
    UUID cartId,
    String customerId,
    Money totalAmount,
    int itemCount,
    List<ItemInfo> items,
    Instant occurredOn)
    implements IntegrationEvent {

  /**
   * Lightweight DTO for item information.
   *
   * @param productId the product ID from Shared Kernel
   * @param quantity the quantity
   */
  public record ItemInfo(ProductId productId, int quantity) {}
}
