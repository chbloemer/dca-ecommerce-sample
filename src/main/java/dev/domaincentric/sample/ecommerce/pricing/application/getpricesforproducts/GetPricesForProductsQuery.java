package dev.domaincentric.sample.ecommerce.pricing.application.getpricesforproducts;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.Collection;

/**
 * Input model for retrieving prices for multiple products.
 *
 * @param productIds the collection of product IDs to get prices for
 */
public record GetPricesForProductsQuery(Collection<ProductId> productIds) {}
