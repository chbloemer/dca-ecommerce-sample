package de.sample.aiarchitecture.account.application.getprofile;

import de.sample.aiarchitecture.sharedkernel.marker.port.in.UseCase;

/** Input port for reading the profile of the currently authenticated account. */
public interface GetProfileInputPort extends UseCase<GetProfileQuery, GetProfileResult> {}
