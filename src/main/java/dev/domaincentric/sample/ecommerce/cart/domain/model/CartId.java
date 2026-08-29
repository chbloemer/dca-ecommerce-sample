package dev.domaincentric.sample.ecommerce.cart.domain.model;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.Id;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.Value;
import java.util.UUID;

/** Value Object representing a Shopping Cart's unique identifier. */
public record CartId(String value) implements Id, Value {

  public CartId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("CartId cannot be null or blank");
    }
  }

  public static CartId generate() {
    return new CartId(UUID.randomUUID().toString());
  }

  public static CartId of(final String value) {
    return new CartId(value);
  }
}
