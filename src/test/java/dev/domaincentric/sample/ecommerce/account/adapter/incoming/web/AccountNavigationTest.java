package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.domaincentric.sample.ecommerce.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link AccountNavigation}.
 *
 * <p>Pins the account area's site map: four items in a fixed order, profile and change-password
 * real links, orders still a non-navigable placeholder, and exactly one item active per page.
 */
@DisplayName("AccountNavigation")
class AccountNavigationTest {

  @Test
  @DisplayName("returns exactly four items in order overview, profile, change-password, orders")
  void returnsFourItemsInOrder() {
    final List<NavItem> items = AccountNavigation.itemsWithActive(AccountNavigation.OVERVIEW);

    assertEquals(4, items.size(), "the account area has exactly four nav items");
    assertEquals(
        List.of(
            AccountNavigation.OVERVIEW,
            AccountNavigation.PROFILE,
            AccountNavigation.CHANGE_PASSWORD,
            AccountNavigation.ORDERS),
        items.stream().map(NavItem::key).toList());
  }

  @Test
  @DisplayName("the change-password item has href /account/change-password")
  void changePasswordItemHasHref() {
    assertEquals(
        "/account/change-password",
        item(AccountNavigation.CHANGE_PASSWORD).href(),
        "the change-password item must link to the change password page");
  }

  @Test
  @DisplayName("the change-password item is navigable")
  void changePasswordItemIsNavigable() {
    assertTrue(
        item(AccountNavigation.CHANGE_PASSWORD).navigable(),
        "the change-password item is no longer a disabled placeholder");
  }

  @Test
  @DisplayName("the change-password item has the label 'Change Password'")
  void changePasswordItemHasLabel() {
    assertEquals("Change Password", item(AccountNavigation.CHANGE_PASSWORD).label());
  }

  @Test
  @DisplayName("the profile item has href /account/profile")
  void profileItemHasHref() {
    assertEquals(
        "/account/profile",
        item(AccountNavigation.PROFILE).href(),
        "the profile item must link to the profile page");
  }

  @Test
  @DisplayName("the profile item is navigable")
  void profileItemIsNavigable() {
    assertTrue(
        item(AccountNavigation.PROFILE).navigable(),
        "the profile item is no longer a disabled placeholder");
  }

  @Test
  @DisplayName("the profile item has the label 'My Profile'")
  void profileItemHasLabel() {
    assertEquals("My Profile", item(AccountNavigation.PROFILE).label());
  }

  @Test
  @DisplayName("orders remains a non-navigable placeholder without href")
  void ordersRemainsPlaceholder() {
    assertNull(item(AccountNavigation.ORDERS).href(), "the orders item must not carry an href yet");
    assertFalse(
        item(AccountNavigation.ORDERS).navigable(), "the orders item must not be navigable yet");
  }

  @Test
  @DisplayName("the overview item still has href /account")
  void overviewItemStillLinksToAccount() {
    assertEquals("/account", item(AccountNavigation.OVERVIEW).href());
  }

  private static NavItem item(final String key) {
    return AccountNavigation.itemsWithActive(AccountNavigation.OVERVIEW).stream()
        .filter(navItem -> key.equals(navItem.key()))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Missing nav item: " + key));
  }

  @ParameterizedTest
  @ValueSource(strings = {"overview", "profile", "change-password", "orders"})
  @DisplayName("marks exactly the requested item as active")
  void marksRequestedItemActive(final String activeKey) {
    final List<NavItem> items = AccountNavigation.itemsWithActive(activeKey);

    assertEquals(
        List.of(activeKey),
        items.stream().filter(NavItem::active).map(NavItem::key).toList(),
        "exactly the requested item is active");
  }

  @Test
  @DisplayName("marks no item active for a page outside the account area")
  void marksNoItemActiveForUnknownKey() {
    assertTrue(
        AccountNavigation.itemsWithActive("not-an-account-page").stream()
            .noneMatch(NavItem::active));
  }
}
