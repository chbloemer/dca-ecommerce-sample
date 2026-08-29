package dev.domaincentric.sample.ecommerce.product.application.createproduct;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/**
 * Input port for creating a new product.
 *
 * <p>This port defines the contract for creating products in the Product bounded context. Primary
 * adapters (REST controllers, MCP tools, etc.) depend on this interface, not the concrete use case
 * implementation.
 *
 * <p><b>Hexagonal Architecture:</b> This is a driving/primary port - it drives the application core
 * to execute business logic.
 *
 * @see
 *     dev.domaincentric.sample.ecommerce.product.application.usecase.createproduct.CreateProductUseCase
 */
public interface CreateProductInputPort extends UseCase<CreateProductCommand, CreateProductResult> {

  /**
   * Creates a new product.
   *
   * @param command the command containing product details
   * @return the created product details
   * @throws IllegalArgumentException if SKU already exists or validation fails
   */
  @Override
  CreateProductResult execute(CreateProductCommand command);
}
