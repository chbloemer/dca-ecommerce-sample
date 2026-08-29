package dev.domaincentric.sample.ecommerce.account.application.changeprofile;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Output model of the Change Profile use case.
 *
 * <p>Per ADR-023 a rejected profile change is a normal return value, not an exception crossing the
 * port.
 *
 * @param outcome what happened
 * @param errorMessage the message to display, present exactly for the rejecting outcomes {@link
 *     Outcome#EMAIL_ALREADY_IN_USE} and {@link Outcome#INPUT_REJECTED}
 * @param profile the stored profile after the change, present exactly for {@link Outcome#CHANGED}
 */
public record ChangeProfileResult(
    Outcome outcome, Optional<String> errorMessage, Optional<Profile> profile) {

  public ChangeProfileResult {
    if (outcome == null) {
      throw new IllegalArgumentException("Outcome cannot be null");
    }
    if (errorMessage == null || profile == null) {
      throw new IllegalArgumentException("Optionals cannot be null, use Optional.empty()");
    }
    final boolean rejecting =
        outcome == Outcome.EMAIL_ALREADY_IN_USE || outcome == Outcome.INPUT_REJECTED;
    if (rejecting != errorMessage.isPresent()) {
      throw new IllegalArgumentException(
          "A rejecting outcome must name its reason, any other outcome must not: " + outcome);
    }
    if ((outcome == Outcome.CHANGED) != profile.isPresent()) {
      throw new IllegalArgumentException(
          "Only a stored change carries the profile, and it always does: " + outcome);
    }
  }

  /** Possible outcomes of a profile change attempt. */
  public enum Outcome {
    /** The profile was stored. */
    CHANGED,
    /** No account for the given UserId, or the account cannot log in. */
    ACCOUNT_NOT_ACCESSIBLE,
    /** Another account already uses the submitted email address. */
    EMAIL_ALREADY_IN_USE,
    /** The submitted values are not valid profile information. */
    INPUT_REJECTED
  }

  public static ChangeProfileResult changed(final Profile profile) {
    return new ChangeProfileResult(Outcome.CHANGED, Optional.empty(), Optional.of(profile));
  }

  public static ChangeProfileResult accountNotAccessible() {
    return new ChangeProfileResult(
        Outcome.ACCOUNT_NOT_ACCESSIBLE, Optional.empty(), Optional.empty());
  }

  public static ChangeProfileResult emailAlreadyInUse(final String message) {
    return new ChangeProfileResult(
        Outcome.EMAIL_ALREADY_IN_USE, Optional.of(message), Optional.empty());
  }

  /**
   * @param message the domain's rejection text, rendered to the user verbatim
   */
  public static ChangeProfileResult inputRejected(final String message) {
    return new ChangeProfileResult(Outcome.INPUT_REJECTED, Optional.of(message), Optional.empty());
  }

  /**
   * The stored profile after a successful change.
   *
   * @param email the stored email address
   * @param dateOfBirth the stored date of birth of the account's owner
   */
  public record Profile(String email, LocalDate dateOfBirth) {}
}
