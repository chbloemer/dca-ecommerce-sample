package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Domain Event indicating that the date of birth of an account's owner was corrected.
 *
 * <p>The date of birth belongs to the {@code Owner}, not to the account — the account is only the
 * aggregate that guards the change. There is deliberately no counterpart for the owner's name: the
 * name is captured once at registration and no operation changes it.
 *
 * <p>Carries both dates, like {@link AccountEmailChanged}: a correction is only interpretable
 * against the value it replaced.
 *
 * @param previousDateOfBirth the date of birth as it was stored before the correction
 * @param newDateOfBirth the owner's corrected date of birth
 */
public record AccountOwnerDateOfBirthChanged(
    UUID eventId,
    AccountId accountId,
    LocalDate previousDateOfBirth,
    LocalDate newDateOfBirth,
    Instant occurredOn)
    implements DomainEvent {

  public static AccountOwnerDateOfBirthChanged now(
      final AccountId accountId,
      final LocalDate previousDateOfBirth,
      final LocalDate newDateOfBirth) {
    return new AccountOwnerDateOfBirthChanged(
        UUID.randomUUID(), accountId, previousDateOfBirth, newDateOfBirth, Instant.now());
  }
}
