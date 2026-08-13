package de.sample.aiarchitecture.account.application.changeprofile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileResult.Outcome;
import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileResult.Profile;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Unit tests for {@link ChangeProfileResult}.
 *
 * <p>Pins that every rejection is a return value rather than an exception crossing the port, and
 * that the result cannot be built in a state that contradicts its outcome.
 */
@DisplayName("ChangeProfileResult")
class ChangeProfileResultTest {

  private static final Profile PROFILE =
      new Profile("jane.doe@example.com", LocalDate.of(1990, 5, 17));

  @Test
  @DisplayName("a change carries the stored profile and no message")
  void changedCarriesProfile() {
    final ChangeProfileResult result = ChangeProfileResult.changed(PROFILE);

    assertEquals(Outcome.CHANGED, result.outcome());
    assertEquals(Optional.of(PROFILE), result.profile());
    assertTrue(result.errorMessage().isEmpty());
  }

  @ParameterizedTest
  @EnumSource(
      value = Outcome.class,
      names = {"EMAIL_ALREADY_IN_USE", "INPUT_REJECTED"})
  @DisplayName("a rejecting outcome without a reason is refused")
  void rejectingOutcomeNeedsReason(final Outcome outcome) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeProfileResult(outcome, Optional.empty(), Optional.empty()));
  }

  @ParameterizedTest
  @EnumSource(
      value = Outcome.class,
      names = {"CHANGED", "ACCOUNT_NOT_ACCESSIBLE"})
  @DisplayName("a non-rejecting outcome carrying a reason is refused")
  void nonRejectingOutcomeCarriesNoReason(final Outcome outcome) {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeProfileResult(outcome, Optional.of("boom"), Optional.of(PROFILE)));
  }

  @Test
  @DisplayName("a change without the stored profile is refused")
  void changeNeedsProfile() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeProfileResult(Outcome.CHANGED, Optional.empty(), Optional.empty()));
  }

  @Test
  @DisplayName("a rejection carrying a profile is refused")
  void rejectionCarriesNoProfile() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ChangeProfileResult(
                Outcome.INPUT_REJECTED, Optional.of("boom"), Optional.of(PROFILE)));
  }

  @Test
  @DisplayName("an inaccessible account carries neither a message nor a profile")
  void accountNotAccessibleCarriesNothing() {
    final ChangeProfileResult result = ChangeProfileResult.accountNotAccessible();

    assertEquals(Outcome.ACCOUNT_NOT_ACCESSIBLE, result.outcome());
    assertTrue(result.errorMessage().isEmpty());
    assertTrue(result.profile().isEmpty());
  }

  @Test
  @DisplayName("a null optional is refused instead of being treated as absent")
  void nullOptionalIsRefused() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeProfileResult(Outcome.ACCOUNT_NOT_ACCESSIBLE, null, Optional.empty()));
    assertThrows(
        IllegalArgumentException.class,
        () -> new ChangeProfileResult(Outcome.ACCOUNT_NOT_ACCESSIBLE, Optional.empty(), null));
  }
}
