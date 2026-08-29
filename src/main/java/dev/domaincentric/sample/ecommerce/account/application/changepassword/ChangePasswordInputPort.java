package dev.domaincentric.sample.ecommerce.account.application.changepassword;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.in.UseCase;

/** Input port for the Change Password use case. */
public interface ChangePasswordInputPort
    extends UseCase<ChangePasswordCommand, ChangePasswordResult> {}
