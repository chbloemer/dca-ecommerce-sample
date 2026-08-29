/**
 * Inventory Bounded Context.
 *
 * <p>Responsible for managing stock levels and inventory tracking.
 */
@NullMarked
@BoundedContext(name = "Inventory", description = "Stock level management and inventory tracking")
@Partnership(
    context = "checkout",
    rationale =
        "Inventory owns the consumer-defined StockReductionTrigger contract that checkout events"
            + " implement; both contexts evolve it together")
@ApplicationModule(allowedDependencies = {"sharedkernel", "infrastructure"})
package de.sample.aiarchitecture.inventory;

import de.sample.aiarchitecture.sharedkernel.marker.strategic.BoundedContext;
import de.sample.aiarchitecture.sharedkernel.marker.strategic.Partnership;
import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.ApplicationModule;
