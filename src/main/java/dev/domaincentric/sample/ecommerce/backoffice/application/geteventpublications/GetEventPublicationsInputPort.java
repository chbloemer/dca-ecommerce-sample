package dev.domaincentric.sample.ecommerce.backoffice.application.geteventpublications;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.in.UseCase;

/** Input port for retrieving event publications from the Spring Modulith event log. */
public interface GetEventPublicationsInputPort
    extends UseCase<GetEventPublicationsQuery, GetEventPublicationsResult> {

  @Override
  GetEventPublicationsResult execute(GetEventPublicationsQuery query);
}
