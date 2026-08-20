package de.sample.aiarchitecture.account.adapter.incoming.web;

import de.sample.aiarchitecture.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * ViewModel for the "Change Password" page.
 *
 * <p>Carries the left navigation items and the single page message. Success and error both travel
 * through this ViewModel so that the page owns its own message elements instead of the generic
 * alert of the layout. The form fields themselves are never part of the ViewModel — a submitted
 * password must not survive into the model.
 *
 * @param navItems the left navigation items in display order
 * @param successMessage the success message to display, {@code null} if none
 * @param errorMessage the error message to display, {@code null} if none
 */
public record ChangePasswordPageViewModel(
    List<NavItem> navItems, @Nullable String successMessage, @Nullable String errorMessage) {

  public static ChangePasswordPageViewModel blank() {
    return new ChangePasswordPageViewModel(accountNavItems(), null, null);
  }

  public static ChangePasswordPageViewModel withSuccess(final String message) {
    return new ChangePasswordPageViewModel(accountNavItems(), message, null);
  }

  public static ChangePasswordPageViewModel withError(final String message) {
    return new ChangePasswordPageViewModel(accountNavItems(), null, message);
  }

  private static List<NavItem> accountNavItems() {
    return AccountNavigation.itemsWithActive(AccountNavigation.CHANGE_PASSWORD);
  }
}
