package dev.domaincentric.sample.ecommerce.checkout.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Id;
import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;

/**
 * Value Object representing a payment provider's unique identifier.
 *
 * <p>Payment providers are external systems that handle payment processing (e.g., "stripe",
 * "paypal", "invoice").
 */
public record PaymentProviderId(String value) implements Id, Value {

  public PaymentProviderId {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("PaymentProviderId cannot be null or blank");
    }
  }

  public static PaymentProviderId of(final String value) {
    return new PaymentProviderId(value);
  }
}
