package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an account was suspended. */
public record AccountSuspended(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountSuspended now(final AccountId accountId) {
    return new AccountSuspended(UUID.randomUUID(), accountId, Instant.now());
  }
}
