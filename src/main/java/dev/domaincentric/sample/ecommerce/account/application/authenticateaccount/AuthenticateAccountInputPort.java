package dev.domaincentric.sample.ecommerce.account.application.authenticateaccount;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for the Authenticate Account use case. */
public interface AuthenticateAccountInputPort
    extends UseCase<AuthenticateAccountCommand, AuthenticateAccountResult> {}
