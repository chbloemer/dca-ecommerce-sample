package dev.domaincentric.sample.ecommerce.inventory.application.getstockforproducts;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/**
 * Input port for retrieving stock levels for multiple products.
 *
 * <p>This port provides bulk stock lookup for Cart/Checkout to efficiently check availability for
 * multiple products in a single operation.
 *
 * <p><b>Hexagonal Architecture:</b> This is a driving/primary port for read operations.
 *
 * @see GetStockForProductsUseCase
 */
public interface GetStockForProductsInputPort
    extends UseCase<GetStockForProductsQuery, GetStockForProductsResult> {

  /**
   * Retrieves stock information for the specified products.
   *
   * <p>Returns stock data for each product including available quantity and availability status.
   * Products without stock levels will not be included in the result.
   *
   * @param query the query containing product IDs to check
   * @return response containing stock data mapped by product ID
   */
  @Override
  GetStockForProductsResult execute(GetStockForProductsQuery query);
}
