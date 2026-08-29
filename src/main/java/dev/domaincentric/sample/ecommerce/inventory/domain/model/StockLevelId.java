package dev.domaincentric.sample.ecommerce.inventory.domain.model;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.Id;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.Value;
import java.util.UUID;

public record StockLevelId(String value) implements Id, Value {

  public StockLevelId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("StockLevelId cannot be null or blank");
    }
  }

  public static StockLevelId generate() {
    return new StockLevelId(UUID.randomUUID().toString());
  }

  public static StockLevelId of(final String value) {
    return new StockLevelId(value);
  }
}
