package dev.domaincentric.sample.ecommerce.product.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;

/**
 * Value Object representing external article data for a product.
 *
 * <p>This combines pricing data from the Pricing context and stock data from the Inventory context
 * into a single value object for use in enriched product read models.
 *
 * @param currentPrice the current price from the Pricing context
 * @param stockQuantity the available stock from the Inventory context
 * @param isAvailable whether the product is available for purchase
 */
public record ProductArticle(Money currentPrice, int stockQuantity, boolean isAvailable)
    implements Value {

  public ProductArticle {
    if (currentPrice == null) {
      throw new IllegalArgumentException("Current price cannot be null");
    }
    if (stockQuantity < 0) {
      throw new IllegalArgumentException("Stock quantity cannot be negative");
    }
  }

  /**
   * Checks if there is sufficient stock for the given quantity.
   *
   * @param quantity the requested quantity
   * @return true if stock is sufficient
   */
  public boolean hasStockFor(final int quantity) {
    return stockQuantity >= quantity;
  }
}
