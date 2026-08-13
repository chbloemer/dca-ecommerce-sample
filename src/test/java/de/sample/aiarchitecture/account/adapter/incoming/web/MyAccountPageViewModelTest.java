package de.sample.aiarchitecture.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sample.aiarchitecture.account.adapter.incoming.web.AccountNavigation.NavItem;
import de.sample.aiarchitecture.account.application.getaccountoverview.GetAccountOverviewResult.AccountOverview;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MyAccountPageViewModel}.
 *
 * <p>Covers the email the greeting is built from, the four nav items in order, the overview item as
 * the active link to {@code /account}, the profile item as an inactive link, and the absent href on
 * the orders placeholder.
 *
 * <p>The rendered DOM — greeting wording, {@code aria-disabled} attributes, item order in the
 * markup — is covered by {@link AccountOverviewTemplateTest}, not here. No E2E test exists for the
 * account area.
 */
@DisplayName("MyAccountPageViewModel")
class MyAccountPageViewModelTest {

  private static final String EMAIL = "jane.doe@example.com";
  private static final Instant LAST_LOGIN = Instant.parse("2026-07-31T08:15:30Z");

  @Test
  @DisplayName("exposes the email address the greeting is rendered from")
  void exposesEmail() {
    final MyAccountPageViewModel viewModel =
        MyAccountPageViewModel.from(new AccountOverview(EMAIL, LAST_LOGIN));

    assertEquals(EMAIL, viewModel.email());
  }

  @Test
  @DisplayName("renders exactly four nav items in order Overview, Profile, Password, Orders")
  void rendersFourNavItemsInOrder() {
    final List<NavItem> navItems =
        MyAccountPageViewModel.from(new AccountOverview(EMAIL, LAST_LOGIN)).navItems();

    assertEquals(4, navItems.size(), "the account area has exactly four nav items");
    assertEquals(
        List.of(
            AccountNavigation.OVERVIEW,
            AccountNavigation.PROFILE,
            AccountNavigation.CHANGE_PASSWORD,
            AccountNavigation.ORDERS),
        navItems.stream().map(NavItem::key).toList());
    assertEquals(
        List.of("Overview", "My Profile", "Change Password", "My Orders"),
        navItems.stream().map(NavItem::label).toList());
  }

  @Test
  @DisplayName("overview item links to /account and is the active item")
  void overviewItemIsActiveLinkToAccount() {
    final NavItem overview = navItem(AccountNavigation.OVERVIEW);

    assertEquals("/account", overview.href());
    assertTrue(overview.active(), "the overview item is the active item on the overview page");
    assertTrue(overview.navigable(), "the overview item must be navigable");
  }

  @Test
  @DisplayName("profile item links to /account/profile and is not the active item here")
  void profileItemLinksToProfilePage() {
    final NavItem profile = navItem(AccountNavigation.PROFILE);

    assertEquals("/account/profile", profile.href());
    assertTrue(profile.navigable(), "the profile item must be navigable");
    assertFalse(profile.active(), "the profile item is not active on the overview page");
  }

  @Test
  @DisplayName("orders item carries no href and is not active")
  void ordersItemCarriesNoHref() {
    final NavItem orders = navItem(AccountNavigation.ORDERS);

    assertNull(orders.href(), "the orders placeholder must not carry an href");
    assertFalse(orders.navigable(), "the orders placeholder must not be navigable");
    assertFalse(orders.active(), "the orders placeholder must not be active");
  }

  @Test
  @DisplayName("formats the last login for display")
  void formatsLastLoginForDisplay() {
    final MyAccountPageViewModel viewModel =
        MyAccountPageViewModel.from(new AccountOverview(EMAIL, LAST_LOGIN));

    assertEquals(
        "31.07.2026 08:15",
        viewModel.lastLoginDisplay(),
        "lastLoginDisplay must render the known last login timestamp in UTC");
  }

  @Test
  @DisplayName("renders the never-logged-in text when the account has no last login")
  void rendersNeverLoggedInWithoutLastLogin() {
    final MyAccountPageViewModel viewModel =
        MyAccountPageViewModel.from(new AccountOverview(EMAIL, null));

    assertEquals(MyAccountPageViewModel.NEVER_LOGGED_IN, viewModel.lastLoginDisplay());
  }

  private static NavItem navItem(final String key) {
    return MyAccountPageViewModel.from(new AccountOverview(EMAIL, LAST_LOGIN)).navItems().stream()
        .filter(item -> key.equals(item.key()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Missing nav item: " + key));
  }
}
