package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a suspended account was reactivated. */
public record AccountReactivated(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountReactivated now(final AccountId accountId) {
    return new AccountReactivated(UUID.randomUUID(), accountId, Instant.now());
  }
}
