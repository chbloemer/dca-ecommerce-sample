package dev.domaincentric.sample.ecommerce.checkout.application.shared;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.OutputPort;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutArticle;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.Collection;
import java.util.Map;

/**
 * Output port for accessing article data during checkout.
 *
 * <p>This port allows checkout use cases to fetch current article information including pricing and
 * availability. It provides a bulk operation to efficiently retrieve data for multiple products at
 * once.
 *
 * <p><b>Hexagonal Architecture:</b> This is a secondary/driven port that defines what the Checkout
 * application layer needs from external article/product data sources.
 */
public interface CheckoutArticleDataPort extends OutputPort {

  /**
   * Retrieves article data for a collection of product IDs.
   *
   * @param productIds the collection of product IDs to fetch data for
   * @return a map from ProductId to CheckoutArticle for all found products; products not found will
   *     not be included in the map
   */
  Map<ProductId, CheckoutArticle> getArticleData(Collection<ProductId> productIds);
}
