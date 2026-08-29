package dev.domaincentric.sample.ecommerce.product.domain.model;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.Value;

/** Value Object representing a Product's description. */
public record ProductDescription(String value) implements Value {

  public ProductDescription {
    if (value == null) {
      throw new IllegalArgumentException("Product description cannot be null");
    }
    if (value.length() > 2000) {
      throw new IllegalArgumentException("Product description cannot exceed 2000 characters");
    }
  }

  public static ProductDescription of(final String value) {
    return new ProductDescription(value);
  }

  public static ProductDescription empty() {
    return new ProductDescription("");
  }
}
