package dev.domaincentric.sample.ecommerce.product.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;

/** Value Object representing a Product's category. */
public record Category(String name) implements Value {

  public Category {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Category name cannot be null or blank");
    }
  }

  public static Category of(final String name) {
    return new Category(name);
  }

  // Predefined categories of the assortment
  public static Category books() {
    return new Category("Books");
  }

  public static Category modeling() {
    return new Category("Modeling");
  }

  public static Category apparel() {
    return new Category("Apparel");
  }

  public static Category deskAndOffice() {
    return new Category("Desk & Office");
  }

  public static Category stickersAndPins() {
    return new Category("Stickers & Pins");
  }
}
