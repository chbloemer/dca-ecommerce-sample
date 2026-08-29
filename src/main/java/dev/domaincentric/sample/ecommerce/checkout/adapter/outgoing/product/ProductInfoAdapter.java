package dev.domaincentric.sample.ecommerce.checkout.adapter.outgoing.product;

import dev.domaincentric.sample.ecommerce.checkout.application.shared.ProductInfoPort;
import dev.domaincentric.sample.ecommerce.product.api.ProductCatalogService;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements Checkout's ProductInfoPort by delegating to Product context's Open Host
 * Service.
 *
 * <p>This adapter is the ONLY place in Checkout context that imports from Product context,
 * isolating cross-context coupling to the adapter layer.
 */
@Component
public class ProductInfoAdapter implements ProductInfoPort {

  private final ProductCatalogService productCatalogService;

  public ProductInfoAdapter(ProductCatalogService productCatalogService) {
    this.productCatalogService = productCatalogService;
  }

  @Override
  public Optional<String> getProductName(ProductId productId) {
    return productCatalogService
        .getProductInfo(productId)
        .map(ProductCatalogService.ProductInfo::name);
  }

  @Override
  public Optional<String> getProductImageUrl(ProductId productId) {
    return productCatalogService
        .getProductInfo(productId)
        .map(ProductCatalogService.ProductInfo::imageUrl);
  }
}
