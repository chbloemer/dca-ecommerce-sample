/**
 * Shopping Cart Bounded Context.
 *
 * <p>Responsible for cart management, item additions/removals, and cart lifecycle.
 */
@NullMarked
@BoundedContext(
    name = "Shopping Cart",
    description = "Cart management, item additions/removals, and cart lifecycle")
@Upstream(
    context = "product",
    translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
    via = Upstream.Consumes.API,
    rationale =
        "Cart works with its own article snapshot; the catalog model must not leak into cart"
            + " invariants")
@Upstream(
    context = "pricing",
    translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
    via = Upstream.Consumes.API,
    rationale = "Price lookups are translated into the cart's own price representation")
@Upstream(
    context = "inventory",
    translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
    via = Upstream.Consumes.API,
    rationale = "Stock availability is translated into the cart's own article data")
@Partnership(
    context = "checkout",
    rationale =
        "Cart owns the consumer-defined CartCompletionTrigger contract that checkout events"
            + " implement; both contexts evolve it together")
@ApplicationModule(
    allowedDependencies = {
      "sharedkernel",
      "infrastructure",
      "product :: api",
      "pricing :: api",
      "inventory :: api"
    })
package de.sample.aiarchitecture.cart;

import de.sample.aiarchitecture.sharedkernel.marker.strategic.BoundedContext;
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Partnership;
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Upstream;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
