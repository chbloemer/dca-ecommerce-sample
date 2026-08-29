package dev.domaincentric.sample.ecommerce.inventory.events;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.List;

/**
 * Interface for events that trigger stock reduction.
 *
 * <p>This is the consumer-side interface for the Interface Inversion pattern. The Inventory module
 * defines what it needs (a list of order line items), and the producing module (Checkout)
 * implements this interface on its event. This way the Inventory module listens to its own
 * interface, avoiding a dependency on the Checkout module.
 *
 * @see
 *     dev.domaincentric.sample.ecommerce.inventory.adapter.incoming.event.StockReductionEventConsumer
 */
public interface StockReductionTrigger {

  /** The line items for which stock should be reduced. */
  List<OrderLineItem> orderLineItems();

  /**
   * A single line item to reduce stock for.
   *
   * @param productId the product ID
   * @param quantity the quantity to reduce
   */
  record OrderLineItem(ProductId productId, int quantity) {}
}
