package de.sample.aiarchitecture.account.domain.event;

import de.sample.aiarchitecture.account.domain.model.AccountId;
import de.sample.aiarchitecture.account.domain.model.Email;
import de.sample.aiarchitecture.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/** Domain Event indicating that an account's email address was changed. */
public record AccountEmailChanged(
    UUID eventId, AccountId accountId, Email previousEmail, Email newEmail, Instant occurredOn)
    implements DomainEvent {

  public static AccountEmailChanged now(
      final AccountId accountId, final Email previousEmail, final Email newEmail) {
    return new AccountEmailChanged(
        UUID.randomUUID(), accountId, previousEmail, newEmail, Instant.now());
  }
}
