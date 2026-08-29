package dev.domaincentric.sample.ecommerce.account.application.changepassword;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for the Change Password use case. */
public interface ChangePasswordInputPort
    extends UseCase<ChangePasswordCommand, ChangePasswordResult> {}
