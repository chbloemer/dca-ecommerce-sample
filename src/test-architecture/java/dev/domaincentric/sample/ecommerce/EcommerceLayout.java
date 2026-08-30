package dev.domaincentric.sample.ecommerce;

import dev.domaincentric.dca.archunit.DcaLayout;

/** The base package and DCA layout every architecture test of this sample runs against. */
final class EcommerceLayout {

  static final String BASE_PACKAGE = "dev.domaincentric.sample.ecommerce";

  private EcommerceLayout() {}

  /**
   * This sample follows the DCA defaults — {@code domain}, {@code application}, {@code
   * adapter/incoming}, {@code adapter/outgoing}, {@code infrastructure}, {@code *UseCase}, {@code
   * *Resource}. A project that names them differently adjusts them here, e.g.
   *
   * <pre>{@code
   * DcaLayout.forBasePackage(BASE_PACKAGE)
   *     .withIncomingSubpackage("in")
   *     .withOutgoingSubpackage("out")
   *     .withUseCaseSuffix("ApplicationService")
   *     .allowingInDomain("org.jmolecules..");
   * }</pre>
   */
  static DcaLayout layout() {
    return DcaLayout.forBasePackage(BASE_PACKAGE);
  }
}
