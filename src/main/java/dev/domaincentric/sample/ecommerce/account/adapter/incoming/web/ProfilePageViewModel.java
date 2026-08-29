package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import dev.domaincentric.sample.ecommerce.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * ViewModel for the "My Profile" page.
 *
 * <p>Carries the owner's name for display, the editable field values, the left navigation items and
 * the single page message. The editable values survive an error re-render so the user does not
 * retype them; the name is rendered as text and has no input, because it cannot be changed.
 *
 * @param fullName the owner's name, for display only
 * @param email the email value to pre-fill
 * @param dateOfBirth the date-of-birth value to pre-fill, in the ISO format the date input expects
 * @param navItems the left navigation items in display order
 * @param successMessage the success message to display, {@code null} if none
 * @param errorMessage the error message to display, {@code null} if none
 */
public record ProfilePageViewModel(
    String fullName,
    String email,
    String dateOfBirth,
    List<NavItem> navItems,
    @Nullable String successMessage,
    @Nullable String errorMessage) {

  public static ProfilePageViewModel of(
      final String fullName, final String email, final LocalDate dateOfBirth) {
    return new ProfilePageViewModel(
        fullName, email, dateOfBirth.toString(), accountNavItems(), null, null);
  }

  public static ProfilePageViewModel withSuccess(
      final String fullName,
      final String email,
      final LocalDate dateOfBirth,
      final String message) {
    return new ProfilePageViewModel(
        fullName, email, dateOfBirth.toString(), accountNavItems(), message, null);
  }

  /**
   * Re-renders a rejected submission.
   *
   * <p>Takes the date as the raw submitted string, not as a {@code LocalDate}: a submission can be
   * rejected precisely because its date could not be read, and that value must still make it back
   * into the form.
   *
   * @param dateOfBirth the date value as the browser submitted it
   */
  public static ProfilePageViewModel withError(
      final String fullName, final String email, final String dateOfBirth, final String message) {
    return new ProfilePageViewModel(fullName, email, dateOfBirth, accountNavItems(), null, message);
  }

  private static List<NavItem> accountNavItems() {
    return AccountNavigation.itemsWithActive(AccountNavigation.PROFILE);
  }
}
