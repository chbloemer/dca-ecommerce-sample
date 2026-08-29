package dev.domaincentric.sample.ecommerce.account.application.registeraccount;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for the Register Account use case. */
public interface RegisterAccountInputPort
    extends UseCase<RegisterAccountCommand, RegisterAccountResult> {}
