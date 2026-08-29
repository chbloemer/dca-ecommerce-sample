package dev.domaincentric.sample.ecommerce.checkout.adapter.outgoing.product;

import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutArticleDataPort;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutArticle;
import dev.domaincentric.sample.ecommerce.inventory.api.InventoryService;
import dev.domaincentric.sample.ecommerce.inventory.api.InventoryService.StockInfo;
import dev.domaincentric.sample.ecommerce.pricing.api.PricingService;
import dev.domaincentric.sample.ecommerce.pricing.api.PricingService.PriceInfo;
import dev.domaincentric.sample.ecommerce.product.api.ProductCatalogService;
import dev.domaincentric.sample.ecommerce.product.api.ProductCatalogService.ProductInfo;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Composite adapter that aggregates article data from multiple Open Host Services for checkout.
 *
 * <p>This adapter implements Checkout's CheckoutArticleDataPort by delegating to three OHS:
 *
 * <ul>
 *   <li>ProductCatalogService - for product names (identity/description)
 *   <li>PricingService - for current prices
 *   <li>InventoryService - for stock availability
 * </ul>
 *
 * <p>This adapter is the ONLY place in Checkout context that imports from Product, Pricing, and
 * Inventory contexts, isolating cross-context coupling to the adapter layer.
 *
 * <p><b>Hexagonal Architecture:</b> This is an outgoing adapter that implements an output port by
 * delegating to incoming adapters (OHS) of other contexts.
 */
@Component
public class CompositeCheckoutArticleDataAdapter implements CheckoutArticleDataPort {

  private final ProductCatalogService productCatalogService;
  private final PricingService pricingService;
  private final InventoryService inventoryService;

  public CompositeCheckoutArticleDataAdapter(
      ProductCatalogService productCatalogService,
      PricingService pricingService,
      InventoryService inventoryService) {
    this.productCatalogService = productCatalogService;
    this.pricingService = pricingService;
    this.inventoryService = inventoryService;
  }

  @Override
  public Map<ProductId, CheckoutArticle> getArticleData(Collection<ProductId> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      return Map.of();
    }

    // Fetch data from all three OHS services
    Map<ProductId, PriceInfo> prices = pricingService.getPrices(productIds);
    Map<ProductId, StockInfo> stocks = inventoryService.getStock(productIds);

    Map<ProductId, CheckoutArticle> result = new HashMap<>();

    for (ProductId productId : productIds) {
      // ProductCatalogService doesn't have bulk fetch, so fetch individually
      Optional<ProductInfo> productInfo = productCatalogService.getProductInfo(productId);

      // Only include if we have product info (name is required)
      if (productInfo.isPresent()) {
        PriceInfo priceInfo = prices.get(productId);
        if (priceInfo == null) {
          throw new IllegalStateException(
              "Pricing data not available for product: "
                  + productId.value()
                  + ". Ensure price is set in Pricing context.");
        }

        CheckoutArticle article =
            buildCheckoutArticle(productId, productInfo.get(), priceInfo, stocks.get(productId));
        result.put(productId, article);
      }
    }

    return result;
  }

  /** Builds a CheckoutArticle domain object from data fetched from multiple sources. */
  private CheckoutArticle buildCheckoutArticle(
      ProductId productId, ProductInfo productInfo, PriceInfo priceInfo, StockInfo stockInfo) {

    String name = productInfo.name();
    Money currentPrice = priceInfo.currentPrice();

    // Use Inventory context as the single source of truth for stock
    int availableStock = stockInfo != null ? stockInfo.availableStock() : 0;
    boolean isAvailable = stockInfo != null && stockInfo.isAvailable();

    String imageUrl = productInfo.imageUrl();

    return CheckoutArticle.of(productId, name, currentPrice, availableStock, isAvailable, imageUrl);
  }
}
