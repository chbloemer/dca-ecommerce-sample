package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an account was permanently closed. */
public record AccountClosed(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountClosed now(final AccountId accountId) {
    return new AccountClosed(UUID.randomUUID(), accountId, Instant.now());
  }
}
