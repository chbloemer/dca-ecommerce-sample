package dev.domaincentric.sample.ecommerce.account.application.getaccountoverview;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for the Get Account Overview use case. */
public interface GetAccountOverviewInputPort
    extends UseCase<GetAccountOverviewQuery, GetAccountOverviewResult> {}
