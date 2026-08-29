package dev.domaincentric.sample.ecommerce.pricing.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Id;
import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;
import java.util.UUID;

/** Value Object representing a Price's unique identifier. */
public record PriceId(String value) implements Id, Value {

  public PriceId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("PriceId cannot be null or blank");
    }
  }

  public static PriceId generate() {
    return new PriceId(UUID.randomUUID().toString());
  }

  public static PriceId of(final String value) {
    return new PriceId(value);
  }
}
