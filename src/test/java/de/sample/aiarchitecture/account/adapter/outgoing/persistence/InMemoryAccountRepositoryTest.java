package de.sample.aiarchitecture.account.adapter.outgoing.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sample.aiarchitecture.account.domain.model.Account;
import de.sample.aiarchitecture.account.domain.model.AccountId;
import de.sample.aiarchitecture.account.domain.model.AccountStatus;
import de.sample.aiarchitecture.account.domain.model.Email;
import de.sample.aiarchitecture.account.domain.model.HashedPassword;
import de.sample.aiarchitecture.account.domain.model.Owner;
import de.sample.aiarchitecture.sharedkernel.domain.model.UserId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InMemoryAccountRepository}.
 *
 * <p>Pins the email lookup after an account changed its address: the superseded address must stop
 * resolving to the account, otherwise it would keep logging in and the uniqueness check would be
 * wrong.
 */
@DisplayName("InMemoryAccountRepository")
class InMemoryAccountRepositoryTest {

  private static final AccountId ACCOUNT_ID = AccountId.of("account-1");
  private static final String USER_ID = "user-4711";
  private static final String EMAIL = "jane.doe@example.com";
  private static final String NEW_EMAIL = "jane.new@example.com";
  private static final Owner OWNER = Owner.of("Jane", "Doe", LocalDate.of(1990, 5, 17));

  private InMemoryAccountRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryAccountRepository();
    repository.save(accountWith(EMAIL));
  }

  private static Account accountWith(final String email) {
    return Account.reconstitute(
        ACCOUNT_ID,
        Email.of(email),
        OWNER,
        UserId.of(USER_ID),
        HashedPassword.of("hashed:OldPassw0rd"),
        AccountStatus.ACTIVE,
        Set.of("CUSTOMER"),
        Instant.parse("2026-01-01T00:00:00Z"),
        Instant.parse("2026-07-31T08:15:30Z"));
  }

  @Test
  @DisplayName("finds a saved account by its email address")
  void findsAccountByEmail() {
    assertEquals(ACCOUNT_ID, repository.findByEmail(Email.of(EMAIL)).orElseThrow().id());
  }

  @Test
  @DisplayName("finds the account under its new address after the email changed")
  void findsAccountUnderNewEmail() {
    repository.save(accountWith(NEW_EMAIL));

    assertEquals(ACCOUNT_ID, repository.findByEmail(Email.of(NEW_EMAIL)).orElseThrow().id());
  }

  @Test
  @DisplayName("the superseded email address no longer resolves to the account")
  void supersededEmailNoLongerResolves() {
    repository.save(accountWith(NEW_EMAIL));

    assertTrue(
        repository.findByEmail(Email.of(EMAIL)).isEmpty(),
        "the email index must drop the address the account no longer uses");
  }

  @Test
  @DisplayName("the superseded address is dropped when the stored account itself changed it")
  void supersededEmailNoLongerResolvesAfterInPlaceChange() {
    final Account stored = repository.findByEmail(Email.of(EMAIL)).orElseThrow();
    stored.changeEmail(Email.of(NEW_EMAIL));

    repository.save(stored);

    assertTrue(
        repository.findByEmail(Email.of(EMAIL)).isEmpty(),
        "the index must drop the old address even when the changed account is the stored instance");
    assertEquals(ACCOUNT_ID, repository.findByEmail(Email.of(NEW_EMAIL)).orElseThrow().id());
  }

  @Test
  @DisplayName("still finds the account by its linked userId after the email changed")
  void findsAccountByLinkedUserIdAfterEmailChange() {
    repository.save(accountWith(NEW_EMAIL));

    assertEquals(ACCOUNT_ID, repository.findByLinkedUserId(UserId.of(USER_ID)).orElseThrow().id());
  }
}
