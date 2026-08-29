/**
 * Product Catalog Bounded Context.
 *
 * <p>Responsible for product management, catalog browsing, and inventory tracking.
 */
@NullMarked
@BoundedContext(
    name = "Product Catalog",
    description = "Product management, catalog browsing, and inventory tracking")
@Upstream(
    context = "pricing",
    translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
    via = Upstream.Consumes.API,
    rationale = "Prices are translated into the catalog's own product presentation data")
@Upstream(
    context = "inventory",
    translation = Upstream.Translation.ANTI_CORRUPTION_LAYER,
    via = Upstream.Consumes.API,
    rationale = "Stock levels are translated into the catalog's own availability data")
@ApplicationModule(
    allowedDependencies = {"sharedkernel", "infrastructure", "pricing :: api", "inventory :: api"})
package dev.domaincentric.sample.ecommerce.product;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.strategic.BoundedContext;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.strategic.Upstream;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
