package dev.domaincentric.sample.ecommerce.account.application.shared;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.Repository;
import dev.domaincentric.sample.ecommerce.account.domain.model.Account;
import dev.domaincentric.sample.ecommerce.account.domain.model.AccountId;
import dev.domaincentric.sample.ecommerce.account.domain.model.Email;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.UserId;
import java.util.Optional;

/**
 * Repository interface for Account aggregate.
 *
 * <p>Provides collection-like access to Account aggregates using domain language. Implementation
 * resides in the secondary adapter layer.
 *
 * <p>Extends the base {@link Repository} interface which provides:
 *
 * <ul>
 *   <li>{@code findById(AccountId)} - find by aggregate ID
 *   <li>{@code save(Account)} - persist an account
 *   <li>{@code deleteById(AccountId)} - remove an account
 * </ul>
 */
public interface AccountRepository extends Repository<Account, AccountId> {

  /**
   * Finds an account by email address.
   *
   * <p>Email addresses are unique across all accounts.
   *
   * @param email the email address
   * @return the account if found, empty otherwise
   */
  Optional<Account> findByEmail(Email email);

  /**
   * Finds an account by its linked UserId.
   *
   * <p>Each account is linked to exactly one UserId.
   *
   * @param userId the linked user ID
   * @return the account if found, empty otherwise
   */
  Optional<Account> findByLinkedUserId(UserId userId);

  /**
   * Checks if an email is already registered.
   *
   * @param email the email to check
   * @return true if an account with this email exists
   */
  default boolean existsByEmail(final Email email) {
    return findByEmail(email).isPresent();
  }
}
