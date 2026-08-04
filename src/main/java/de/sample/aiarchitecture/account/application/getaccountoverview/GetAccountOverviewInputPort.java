package de.sample.aiarchitecture.account.application.getaccountoverview;

import de.sample.aiarchitecture.sharedkernel.marker.port.in.UseCase;

/** Input port for the Get Account Overview use case. */
public interface GetAccountOverviewInputPort
    extends UseCase<GetAccountOverviewQuery, GetAccountOverviewResult> {}
