package dev.domaincentric.sample.ecommerce.product.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;

/** Value Object representing a Product's name. */
public record ProductName(String value) implements Value {

  public ProductName {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("Product name cannot be null or blank");
    }
    if (value.length() > 255) {
      throw new IllegalArgumentException("Product name cannot exceed 255 characters");
    }
  }

  public static ProductName of(final String value) {
    return new ProductName(value);
  }
}
