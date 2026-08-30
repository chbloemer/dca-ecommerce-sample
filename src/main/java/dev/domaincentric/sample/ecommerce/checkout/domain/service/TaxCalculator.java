package dev.domaincentric.sample.ecommerce.checkout.domain.service;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainService;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Domain Service for the value-added tax contained in a checkout's totals.
 *
 * <p>Line-item prices and shipping costs are gross amounts, so the tax is extracted rather than
 * added: the grand total stays subtotal plus shipping, and the tax line tells the customer how much
 * of it goes to the tax authority.
 *
 * <p>The Cart context owns the same rule for its own page. Duplicating a rule this small keeps the
 * two contexts independent — neither has to change when the other's presentation does.
 */
public final class TaxCalculator implements DomainService {

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
}
