package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that a user logged in to their account. */
public record AccountLoggedIn(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountLoggedIn now(final AccountId accountId) {
    return new AccountLoggedIn(UUID.randomUUID(), accountId, Instant.now());
  }
}
