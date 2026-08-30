package dev.domaincentric.sample.ecommerce;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Spring Modulith module structure verification.
 *
 * <p>Validates that all application modules respect their declared boundaries: no undeclared
 * cross-module dependencies, named interfaces ({@code api/}, {@code events/}) properly configured,
 * {@code allowedDependencies} respected.
 */
class SpringModulithVerificationTest {

  private static final ApplicationModules MODULES =
      ApplicationModules.of(EcommerceLayout.BASE_PACKAGE);

  @Test
  void applicationModuleStructureIsValid() {
    MODULES.verify();
  }

  @Test
  void diagnosticDisplayDiscoveredApplicationModules() {
    var moduleList = MODULES.stream().toList();
    System.out.println("=== Spring Modulith Application Modules ===");
    moduleList.forEach(
        module -> {
          System.out.println("  " + module.getName() + ": " + module.getBasePackage());
          module
              .getNamedInterfaces()
              .forEach(
                  ni -> System.out.println("    Named Interface: " + ni.getName() + " -> " + ni));
        });
    System.out.println("============================================");
    assertFalse(moduleList.isEmpty());
  }
}
