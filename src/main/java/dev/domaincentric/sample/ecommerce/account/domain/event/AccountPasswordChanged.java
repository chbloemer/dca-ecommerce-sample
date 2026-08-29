package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.dca.buildingblocks.ddd.tactical.DomainEvent;
import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an account's password was changed. */
public record AccountPasswordChanged(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountPasswordChanged now(final AccountId accountId) {
    return new AccountPasswordChanged(UUID.randomUUID(), accountId, Instant.now());
  }
}
