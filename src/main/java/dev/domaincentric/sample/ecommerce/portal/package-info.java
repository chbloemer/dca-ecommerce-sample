/**
 * Portal Bounded Context.
 *
 * <p>Responsible for web portal, user interface composition, and cross-context views.
 */
@NullMarked
@BoundedContext(
    name = "Portal",
    description = "Web portal, user interface composition, and cross-context views")
@ApplicationModule(allowedDependencies = {"sharedkernel", "infrastructure"})
package dev.domaincentric.sample.ecommerce.portal;

import dev.domaincentric.dca.buildingblocks.ddd.strategic.BoundedContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
