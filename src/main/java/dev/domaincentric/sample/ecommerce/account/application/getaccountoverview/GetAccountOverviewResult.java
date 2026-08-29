package dev.domaincentric.sample.ecommerce.account.application.getaccountoverview;

import java.time.Instant;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Output model for the account overview.
 *
 * <p>Wraps the projection in an {@link Optional} per ADR-023: an identity without an accessible
 * account is a normal return value, not an exception.
 *
 * @param account the account overview projection, empty if no accessible account exists
 */
public record GetAccountOverviewResult(Optional<AccountOverview> account) {

  public GetAccountOverviewResult {
    if (account == null) {
      throw new IllegalArgumentException("Account optional cannot be null, use Optional.empty()");
    }
  }

  public boolean found() {
    return account.isPresent();
  }

  public static GetAccountOverviewResult found(final AccountOverview account) {
    return new GetAccountOverviewResult(Optional.of(account));
  }

  public static GetAccountOverviewResult notFound() {
    return new GetAccountOverviewResult(Optional.empty());
  }

  /**
   * Projection of the fields the account overview page renders.
   *
   * @param email the account's email address
   * @param lastLoginAt when the user last logged in, {@code null} if never
   */
  public record AccountOverview(String email, @Nullable Instant lastLoginAt) {

    public AccountOverview {
      if (email == null || email.isBlank()) {
        throw new IllegalArgumentException("Email is required for an account overview");
      }
    }
  }
}
