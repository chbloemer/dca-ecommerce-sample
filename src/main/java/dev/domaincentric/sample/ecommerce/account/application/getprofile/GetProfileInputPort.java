package dev.domaincentric.sample.ecommerce.account.application.getprofile;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.in.UseCase;

/** Input port for reading the profile of the currently authenticated account. */
public interface GetProfileInputPort extends UseCase<GetProfileQuery, GetProfileResult> {}
