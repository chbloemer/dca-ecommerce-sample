/**
 * Pricing Bounded Context.
 *
 * <p>Responsible for managing product prices, price changes, and exposing pricing information to
 * other bounded contexts via Open Host Service.
 */
@NullMarked
@BoundedContext(
    name = "Pricing",
    description = "Product pricing management and price change tracking")
@ApplicationModule(allowedDependencies = {"sharedkernel", "infrastructure"})
package dev.domaincentric.sample.ecommerce.pricing;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.strategic.BoundedContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
