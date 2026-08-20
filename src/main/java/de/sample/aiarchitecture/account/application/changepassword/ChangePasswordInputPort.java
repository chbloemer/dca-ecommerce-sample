package de.sample.aiarchitecture.account.application.changepassword;

import de.sample.aiarchitecture.sharedkernel.marker.port.in.UseCase;

/** Input port for the Change Password use case. */
public interface ChangePasswordInputPort
    extends UseCase<ChangePasswordCommand, ChangePasswordResult> {}
