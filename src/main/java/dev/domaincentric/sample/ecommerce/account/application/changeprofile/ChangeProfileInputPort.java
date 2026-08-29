package dev.domaincentric.sample.ecommerce.account.application.changeprofile;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for changing the basic profile information of the authenticated account. */
public interface ChangeProfileInputPort
    extends UseCase<ChangeProfileCommand, ChangeProfileResult> {}
