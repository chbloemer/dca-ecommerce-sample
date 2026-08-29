package dev.domaincentric.sample.ecommerce.cart.domain.model;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;

/**
 * Functional interface for resolving current article pricing and availability.
 *
 * <p>This interface allows the cart domain to receive fresh pricing data without depending on
 * external infrastructure. Implementations can fetch data from product services, caching layers, or
 * other sources.
 */
@FunctionalInterface
public interface ArticlePriceResolver {

  /**
   * Resolves the current price and availability for a product.
   *
   * @param productId the product identifier to resolve pricing for
   * @return the current article price information
   */
  ArticlePrice resolve(ProductId productId);
}
