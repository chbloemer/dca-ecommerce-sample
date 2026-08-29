package dev.domaincentric.sample.ecommerce.account.domain.event;

import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.account.domain.model.Email;
import dev.domaincentric.sample.ecommerce.account.domain.model.Owner;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.UserId;
import dev.domaincentric.sample.ecommerce.sharedkernel.marker.tactical.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain event raised when a new account is registered.
 *
 * <p>This event is raised during account creation and can be used by:
 *
 * <ul>
 *   <li>Email service to send welcome email
 *   <li>Analytics to track registrations
 *   <li>Other bounded contexts that need to know about new users
 * </ul>
 *
 * @param eventId unique identifier for this event instance
 * @param accountId the new account's ID
 * @param email the user's email address
 * @param owner the person the account belongs to; carried so a consumer can address them by name
 *     without querying back
 * @param linkedUserId the UserId linked to this account
 * @param occurredOn when the registration occurred
 */
public record AccountRegistered(
    UUID eventId,
    AccountId accountId,
    Email email,
    Owner owner,
    UserId linkedUserId,
    Instant occurredOn)
    implements DomainEvent {

  /**
   * Creates an AccountRegistered event with the current timestamp.
   *
   * @param accountId the new account's ID
   * @param email the user's email address
   * @param owner the person the account belongs to
   * @param linkedUserId the UserId linked to this account
   * @return a new AccountRegistered event
   */
  public static AccountRegistered now(
      final AccountId accountId, final Email email, final Owner owner, final UserId linkedUserId) {
    return new AccountRegistered(
        UUID.randomUUID(), accountId, email, owner, linkedUserId, Instant.now());
  }
}
