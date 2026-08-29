/**
 * Product Catalog API — published interface for cross-module access.
 *
 * <p>Exposes product identity and description data (Open Host Service pattern). Consuming modules
 * should define their own output ports and implement adapters that delegate to this service.
 */
@NamedInterface("api")
@NullMarked
package dev.domaincentric.sample.ecommerce.product.api;

import org.jspecify.annotations.NullMarked;
import org.springframework.modulith.NamedInterface;
