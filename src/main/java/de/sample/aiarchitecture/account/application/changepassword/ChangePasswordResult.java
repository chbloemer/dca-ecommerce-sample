package de.sample.aiarchitecture.account.application.changepassword;

import java.util.Optional;

/**
 * Output model of the Change Password use case.
 *
 * <p>Per ADR-023 a rejected password change is a normal return value, not an exception crossing the
 * port.
 *
 * @param outcome what happened
 * @param errorMessage the message to display, present exactly for the rejecting outcomes {@link
 *     Outcome#CURRENT_PASSWORD_INVALID} and {@link Outcome#NEW_PASSWORD_REJECTED}, empty for {@link
 *     Outcome#CHANGED} and {@link Outcome#ACCOUNT_NOT_ACCESSIBLE}
 */
public record ChangePasswordResult(Outcome outcome, Optional<String> errorMessage) {

  public ChangePasswordResult {
    if (outcome == null) {
      throw new IllegalArgumentException("Outcome cannot be null");
    }
    if (errorMessage == null) {
      throw new IllegalArgumentException(
          "Error message optional cannot be null, use Optional.empty()");
    }
    final boolean rejecting =
        outcome == Outcome.CURRENT_PASSWORD_INVALID || outcome == Outcome.NEW_PASSWORD_REJECTED;
    if (rejecting != errorMessage.isPresent()) {
      throw new IllegalArgumentException(
          "A rejecting outcome must name its reason, any other outcome must not: " + outcome);
    }
  }

  /** Possible outcomes of a change-password attempt. */
  public enum Outcome {
    /** The password was changed. */
    CHANGED,
    /** No account for the given UserId, or the account cannot log in. */
    ACCOUNT_NOT_ACCESSIBLE,
    /** The supplied current password does not match the stored one. */
    CURRENT_PASSWORD_INVALID,
    /** The new password does not meet the domain's strength rules. */
    NEW_PASSWORD_REJECTED
  }

  public static ChangePasswordResult changed() {
    return new ChangePasswordResult(Outcome.CHANGED, Optional.empty());
  }

  public static ChangePasswordResult accountNotAccessible() {
    return new ChangePasswordResult(Outcome.ACCOUNT_NOT_ACCESSIBLE, Optional.empty());
  }

  public static ChangePasswordResult currentPasswordInvalid(final String message) {
    return new ChangePasswordResult(Outcome.CURRENT_PASSWORD_INVALID, Optional.of(message));
  }

  /**
   * @param message the domain's rejection text, rendered to the user verbatim — so only a strength
   *     violation may reach here, never a hasher failure.
   */
  public static ChangePasswordResult newPasswordRejected(final String message) {
    return new ChangePasswordResult(Outcome.NEW_PASSWORD_REJECTED, Optional.of(message));
  }
}
