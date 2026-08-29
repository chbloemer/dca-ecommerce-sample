package dev.domaincentric.sample.ecommerce;

import dev.domaincentric.dca.archunit.DcaLayout;
import dev.domaincentric.dca.archunit.junit.DcaArchitectureTest;

/**
 * The complete Domain-Centric Architecture rule catalog from {@code
 * dev.domaincentric:dca-archunit}, one dynamic test per rule. The rules pin to the {@code
 * dca-building-blocks} markers the domain code implements.
 */
class ArchitectureRulesTest extends DcaArchitectureTest {

  @Override
  protected DcaLayout layout() {
    return DcaLayout.forBasePackage("dev.domaincentric.sample.ecommerce");
  }
}
