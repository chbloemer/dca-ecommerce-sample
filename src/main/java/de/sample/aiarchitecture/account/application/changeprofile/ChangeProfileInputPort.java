package de.sample.aiarchitecture.account.application.changeprofile;

import de.sample.aiarchitecture.sharedkernel.marker.port.in.UseCase;

/** Input port for changing the basic profile information of the authenticated account. */
public interface ChangeProfileInputPort
    extends UseCase<ChangeProfileCommand, ChangeProfileResult> {}
