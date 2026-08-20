package de.sample.aiarchitecture.account.adapter.outgoing.persistence;

import de.sample.aiarchitecture.account.application.shared.AccountRepository;
import de.sample.aiarchitecture.account.domain.model.Account;
import de.sample.aiarchitecture.account.domain.model.AccountId;
import de.sample.aiarchitecture.account.domain.model.Email;
import de.sample.aiarchitecture.sharedkernel.domain.model.UserId;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of AccountRepository, active under the {@code inmemory} profile.
 *
 * <p>{@link JdbcAccountRepository} is the default; this adapter exists to run the account context
 * without a database. Note that the {@code inmemory} profile is only partial today: the cart still
 * persists via JPA, so the application as a whole still needs its datasource.
 *
 * <p>It behaves as though a database were behind it (ADR-031): every account is copied on the way
 * in and on the way out, so callers never share an instance with the store. Handing out the stored
 * aggregate would let a use case that forgets to {@code save} appear to work here and fail against
 * any real persistence.
 */
@Profile("inmemory")
@Repository
public class InMemoryAccountRepository implements AccountRepository {

  private final ConcurrentHashMap<AccountId, Account> accounts = new ConcurrentHashMap<>();

  // Secondary indexes for efficient lookups
  private final ConcurrentHashMap<String, AccountId> emailIndex = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, AccountId> userIdIndex = new ConcurrentHashMap<>();

  @Override
  public Optional<Account> findById(final AccountId id) {
    return Optional.ofNullable(accounts.get(id)).map(InMemoryAccountRepository::copyOf);
  }

  @Override
  public Optional<Account> findByEmail(final Email email) {
    final AccountId accountId = emailIndex.get(email.value());
    if (accountId == null) {
      return Optional.empty();
    }
    return findById(accountId);
  }

  @Override
  public Optional<Account> findByLinkedUserId(final UserId userId) {
    final AccountId accountId = userIdIndex.get(userId.value());
    if (accountId == null) {
      return Optional.empty();
    }
    return findById(accountId);
  }

  @Override
  public Account save(final Account account) {
    // Stored as a copy so that a later mutation of the caller's instance does not reach the store
    // without a save, the way it would not reach a database either.
    accounts.put(account.id(), copyOf(account));

    // Drop every address this account no longer uses: an account that changed its email must stop
    // resolving under the old one, otherwise it would keep logging in under both and would occupy
    // the address for everyone else.
    emailIndex
        .entrySet()
        .removeIf(
            entry ->
                entry.getValue().equals(account.id())
                    && !entry.getKey().equals(account.email().value()));

    // Update indexes
    emailIndex.put(account.email().value(), account.id());
    userIdIndex.put(account.linkedUserId().value(), account.id());

    return account;
  }

  /**
   * Round-trips an account through {@link Account#reconstitute}, the same way loading a row would.
   *
   * <p>Registered-but-unpublished domain events are deliberately not carried over: a stored account
   * is a fact, and re-reading it must not replay what the writer already published.
   */
  private static Account copyOf(final Account account) {
    return Account.reconstitute(
        account.id(),
        account.email(),
        account.owner(),
        account.linkedUserId(),
        account.password(),
        account.status(),
        account.roles(),
        account.createdAt(),
        account.lastLoginAt());
  }

  @Override
  public void deleteById(final AccountId id) {
    final Account account = accounts.remove(id);
    if (account != null) {
      emailIndex.remove(account.email().value());
      userIdIndex.remove(account.linkedUserId().value());
    }
  }
}
