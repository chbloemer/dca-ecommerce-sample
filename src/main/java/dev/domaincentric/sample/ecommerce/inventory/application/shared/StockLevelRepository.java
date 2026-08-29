package dev.domaincentric.sample.ecommerce.inventory.application.shared;

import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevel;
import dev.domaincentric.sample.ecommerce.inventory.domain.model.StockLevelId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.out.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StockLevel aggregate.
 *
 * <p>Provides collection-like access to StockLevel aggregates using domain language. Implementation
 * resides in the secondary adapter layer.
 *
 * <p>Extends the base {@link Repository} interface which provides common methods:
 *
 * <ul>
 *   <li>{@code findById(StockLevelId)} - inherited from base interface
 *   <li>{@code save(StockLevel)} - inherited from base interface
 *   <li>{@code deleteById(StockLevelId)} - inherited from base interface
 * </ul>
 */
public interface StockLevelRepository extends Repository<StockLevel, StockLevelId> {

  /**
   * Finds the stock level for a specific product.
   *
   * @param productId the product ID to search for
   * @return the stock level if found, empty otherwise
   */
  Optional<StockLevel> findByProductId(ProductId productId);

  /**
   * Finds stock levels for multiple products.
   *
   * @param productIds the collection of product IDs to search for
   * @return list of stock levels for the given product IDs
   */
  List<StockLevel> findByProductIds(Collection<ProductId> productIds);
}
