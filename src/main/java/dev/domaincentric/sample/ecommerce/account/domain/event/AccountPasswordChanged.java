package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an account's password was changed. */
public record AccountPasswordChanged(UUID eventId, AccountId accountId, Instant occurredOn)
    implements DomainEvent {

  public static AccountPasswordChanged now(final AccountId accountId) {
    return new AccountPasswordChanged(UUID.randomUUID(), accountId, Instant.now());
  }
}
