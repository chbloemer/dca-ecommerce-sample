package dev.domaincentric.sample.ecommerce.cart.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;

/**
 * Value Object representing current pricing and availability information for an article.
 *
 * <p>This is used by the cart domain to receive fresh pricing data from external sources without
 * coupling to specific infrastructure implementations.
 */
public record ArticlePrice(Money price, boolean isAvailable, int availableStock) implements Value {

  public ArticlePrice {
    if (price == null) {
      throw new IllegalArgumentException("Price cannot be null");
    }
    if (availableStock < 0) {
      throw new IllegalArgumentException("Available stock cannot be negative");
    }
  }
}
