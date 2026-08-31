package dev.domaincentric.sample.ecommerce.pricing.events;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;

/**
 * Interface for events that trigger the creation of a price record.
 *
 * <p>This is the consumer-side interface for the Interface Inversion pattern. The Pricing module
 * defines what it needs (a product and the price it starts with), and the producing module (Product
 * Catalog) implements this interface on its event. This way the Pricing module listens to its own
 * interface, avoiding a dependency on the Product module.
 *
 * @see
 *     dev.domaincentric.sample.ecommerce.pricing.adapter.incoming.event.PriceInitializationEventConsumer
 */
public interface PriceInitializationTrigger {

  /** The product the price belongs to. */
  ProductId productId();

  /** The price the product starts with. */
  Money initialPrice();
}
