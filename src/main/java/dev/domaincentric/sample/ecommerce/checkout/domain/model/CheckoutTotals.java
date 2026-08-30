package dev.domaincentric.sample.ecommerce.checkout.domain.model;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.Value;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import java.util.Currency;

/**
 * Value Object representing the calculated totals for a checkout session.
 *
 * <p>Contains subtotal, shipping, the contained tax and the grand total. Prices are gross prices:
 * {@code tax} is the share of {@code subtotal} and {@code shipping} that is value-added tax, not an
 * additional charge — which is why {@code total} is subtotal plus shipping and does not include it
 * a second time.
 */
public record CheckoutTotals(Money subtotal, Money shipping, Money tax, Money total)
    implements Value {

  public CheckoutTotals {
    if (subtotal == null) {
      throw new IllegalArgumentException("Subtotal cannot be null");
    }
    if (shipping == null) {
      throw new IllegalArgumentException("Shipping cannot be null");
    }
    if (tax == null) {
      throw new IllegalArgumentException("Tax cannot be null");
    }
    if (total == null) {
      throw new IllegalArgumentException("Total cannot be null");
    }
  }

  public static CheckoutTotals of(
      final Money subtotal, final Money shipping, final Money tax, final Money total) {
    return new CheckoutTotals(subtotal, shipping, tax, total);
  }

  public static CheckoutTotals calculate(
      final Money subtotal, final Money shipping, final Money tax) {
    var total = subtotal.add(shipping);
    return new CheckoutTotals(subtotal, shipping, tax, total);
  }

  public static CheckoutTotals zero(final Currency currency) {
    var zero = Money.zero(currency);
    return new CheckoutTotals(zero, zero, zero, zero);
  }

  public CheckoutTotals withShipping(final Money newShipping) {
    return CheckoutTotals.calculate(this.subtotal, newShipping, this.tax);
  }

  public CheckoutTotals withTax(final Money newTax) {
    return CheckoutTotals.calculate(this.subtotal, this.shipping, newTax);
  }
}
