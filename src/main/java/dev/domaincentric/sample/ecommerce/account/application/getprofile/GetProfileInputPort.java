package dev.domaincentric.sample.ecommerce.account.application.getprofile;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for reading the profile of the currently authenticated account. */
public interface GetProfileInputPort extends UseCase<GetProfileQuery, GetProfileResult> {}
