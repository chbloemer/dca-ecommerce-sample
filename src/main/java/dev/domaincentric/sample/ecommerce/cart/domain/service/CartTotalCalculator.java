package dev.domaincentric.sample.ecommerce.cart.domain.service;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainService;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain Service for the value-added tax contained in a cart's amounts.
 *
 * <p>Article prices are gross prices: what the customer sees is what the customer pays. The tax is
 * therefore not added on top, it is <em>extracted</em> from the amount — the cart shows how much of
 * the subtotal is VAT, and the subtotal itself does not change.
 *
 * <p>The Checkout context applies the same rule to its own totals. Both contexts own their copy of
 * it on purpose: the rule is small, and a shared implementation would tie the two contexts together
 * for the sake of one number.
 */
public final class CartTotalCalculator implements DomainService {

  private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.valueOf(0.19); // 19% VAT

  /**
   * Extracts the tax contained in a gross amount at the given rate.
   *
   * @param grossAmount the amount including tax
   * @param taxRate the tax rate (e.g., 0.19 for 19%)
   * @return the tax portion of the gross amount
   */
  public Money containedTax(final Money grossAmount, final BigDecimal taxRate) {
    if (taxRate.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Tax rate cannot be negative");
    }

    final BigDecimal net =
        grossAmount.amount().divide(BigDecimal.ONE.add(taxRate), 10, RoundingMode.HALF_UP);
    final BigDecimal tax = grossAmount.amount().subtract(net);

    return Money.of(tax.setScale(2, RoundingMode.HALF_UP), grossAmount.currency());
  }

  /**
   * Extracts the tax contained in a gross amount at the default rate (19% VAT).
   *
   * @param grossAmount the amount including tax
   * @return the tax portion of the gross amount
   */
  public Money containedTax(final Money grossAmount) {
    return containedTax(grossAmount, DEFAULT_TAX_RATE);
  }

  /**
   * Returns the net amount of a gross amount at the default rate.
   *
   * @param grossAmount the amount including tax
   * @return the amount without the contained tax
   */
  public Money netAmount(final Money grossAmount) {
    return grossAmount.subtract(containedTax(grossAmount));
  }
}
