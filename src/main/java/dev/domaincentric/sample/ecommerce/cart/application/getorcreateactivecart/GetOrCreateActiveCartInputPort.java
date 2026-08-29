package dev.domaincentric.sample.ecommerce.cart.application.getorcreateactivecart;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.in.UseCase;

/**
 * Input Port for getting or creating an active cart for a customer.
 *
 * <p>This port defines the contract for the "Get or Create Active Cart" use case, which retrieves
 * an existing active cart for a customer or creates a new one if none exists.
 */
public interface GetOrCreateActiveCartInputPort
    extends UseCase<GetOrCreateActiveCartCommand, GetOrCreateActiveCartResult> {}
