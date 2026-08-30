package dev.domaincentric.sample.ecommerce.checkout.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TaxCalculator")
class TaxCalculatorTest {

  private static final Currency EUR = Currency.getInstance("EUR");

  private final TaxCalculator calculator = new TaxCalculator();

  private static Money euro(final String amount) {
    return Money.of(new BigDecimal(amount), EUR);
  }

  @Test
  @DisplayName("extracts the VAT contained in a gross amount instead of adding it")
  void extractsContainedTax() {
    assertEquals(euro("19.00"), calculator.containedTax(euro("119.00")));
  }

  @Test
  @DisplayName("contained tax plus net amount is the gross amount again")
  void containedTaxAndNetAddUpToGross() {
    final Money gross = euro("249.95");

    final Money tax = calculator.containedTax(gross);

    assertEquals(gross, gross.subtract(tax).add(tax));
  }

  @Test
  @DisplayName("a zero amount contains no tax")
  void zeroContainsNoTax() {
    assertEquals(euro("0.00"), calculator.containedTax(euro("0.00")));
  }

  @Test
  @DisplayName("honours an explicit rate")
  void honoursExplicitRate() {
    assertEquals(euro("7.00"), calculator.containedTax(euro("107.00"), BigDecimal.valueOf(0.07)));
  }

  @Test
  @DisplayName("rejects a negative rate")
  void rejectsNegativeRate() {
    assertThrows(
        IllegalArgumentException.class,
        () -> calculator.containedTax(euro("100.00"), BigDecimal.valueOf(-0.19)));
  }
}
