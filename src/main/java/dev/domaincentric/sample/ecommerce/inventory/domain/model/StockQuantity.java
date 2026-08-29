package dev.domaincentric.sample.ecommerce.inventory.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;

public record StockQuantity(int value) implements Value {

  public StockQuantity {
    if (value < 0) {
      throw new IllegalArgumentException("Stock quantity cannot be negative");
    }
  }

  public static StockQuantity of(final int value) {
    return new StockQuantity(value);
  }
}
