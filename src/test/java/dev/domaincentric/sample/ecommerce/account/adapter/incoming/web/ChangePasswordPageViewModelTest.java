package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import dev.domaincentric.sample.ecommerce.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ChangePasswordPageViewModel}.
 *
 * <p>Pins that exactly the change-password item is active on every render path.
 */
@DisplayName("ChangePasswordPageViewModel")
class ChangePasswordPageViewModelTest {

  @Test
  @DisplayName("blank() marks exactly the change-password item active")
  void blankMarksChangePasswordActive() {
    assertActiveIsChangePassword(ChangePasswordPageViewModel.blank());
  }

  @Test
  @DisplayName("withSuccess() marks exactly the change-password item active")
  void withSuccessMarksChangePasswordActive() {
    assertActiveIsChangePassword(ChangePasswordPageViewModel.withSuccess("done"));
  }

  @Test
  @DisplayName("withError() marks exactly the change-password item active")
  void withErrorMarksChangePasswordActive() {
    assertActiveIsChangePassword(ChangePasswordPageViewModel.withError("nope"));
  }

  @Test
  @DisplayName("blank() carries neither a success nor an error message")
  void blankCarriesNoMessage() {
    final ChangePasswordPageViewModel viewModel = ChangePasswordPageViewModel.blank();

    assertNull(viewModel.successMessage());
    assertNull(viewModel.errorMessage());
  }

  @Test
  @DisplayName("withSuccess() carries only the success message")
  void withSuccessCarriesOnlySuccessMessage() {
    final ChangePasswordPageViewModel viewModel =
        ChangePasswordPageViewModel.withSuccess("Your password has been changed.");

    assertEquals("Your password has been changed.", viewModel.successMessage());
    assertNull(viewModel.errorMessage());
  }

  @Test
  @DisplayName("withError() carries only the error message")
  void withErrorCarriesOnlyErrorMessage() {
    final ChangePasswordPageViewModel viewModel =
        ChangePasswordPageViewModel.withError("Current password is not correct");

    assertEquals("Current password is not correct", viewModel.errorMessage());
    assertNull(viewModel.successMessage());
  }

  private static void assertActiveIsChangePassword(final ChangePasswordPageViewModel viewModel) {
    final List<NavItem> navItems = viewModel.navItems();

    assertEquals(
        List.of(
            AccountNavigation.OVERVIEW,
            AccountNavigation.PROFILE,
            AccountNavigation.CHANGE_PASSWORD,
            AccountNavigation.ORDERS),
        navItems.stream().map(NavItem::key).toList(),
        "the change password page renders the full account navigation");
    assertEquals(
        List.of(AccountNavigation.CHANGE_PASSWORD),
        navItems.stream().filter(NavItem::active).map(NavItem::key).toList(),
        "exactly the change-password item is active");
  }
}
