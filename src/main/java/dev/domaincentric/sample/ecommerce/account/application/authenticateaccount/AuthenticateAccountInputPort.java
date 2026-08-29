package dev.domaincentric.sample.ecommerce.account.application.authenticateaccount;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.in.UseCase;

/** Input port for the Authenticate Account use case. */
public interface AuthenticateAccountInputPort
    extends UseCase<AuthenticateAccountCommand, AuthenticateAccountResult> {}
