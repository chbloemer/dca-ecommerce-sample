package dev.domaincentric.sample.ecommerce.account.application.getaccountoverview;

import dev.domaincentric.sample.ecommerce.sharedkernel.marker.port.in.UseCase;

/** Input port for the Get Account Overview use case. */
public interface GetAccountOverviewInputPort
    extends UseCase<GetAccountOverviewQuery, GetAccountOverviewResult> {}
