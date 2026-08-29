package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Left navigation of the account area.
 *
 * <p>Owns the site map of the account area so that every page in it renders the same items with its
 * own item marked active. Pages that do not exist yet are listed as non-navigable placeholders.
 */
public final class AccountNavigation {

  public static final String OVERVIEW = "overview";

  /** Navigation item key of the profile page. */
  public static final String PROFILE = "profile";

  public static final String CHANGE_PASSWORD = "change-password";

  /** Navigation item key of the (future) orders page. */
  public static final String ORDERS = "orders";

  private AccountNavigation() {}

  /**
   * Returns the navigation items in display order with the given item marked active.
   *
   * @param activeKey the key of the page currently displayed
   * @return the navigation items in display order
   */
  public static List<NavItem> itemsWithActive(final String activeKey) {
    return List.of(
        new NavItem(OVERVIEW, "Overview", MyAccountPageController.ACCOUNT_PATH, activeKey),
        new NavItem(PROFILE, "My Profile", ProfilePageController.PROFILE_PATH, activeKey),
        new NavItem(
            CHANGE_PASSWORD,
            "Change Password",
            ChangePasswordPageController.CHANGE_PASSWORD_PATH,
            activeKey),
        new NavItem(ORDERS, "My Orders", null, activeKey));
  }

  /**
   * A single left navigation item.
   *
   * <p>Placeholder items for not-yet-implemented pages carry a {@code null} href and are rendered
   * as disabled, non-navigable elements.
   *
   * @param key the stable item key (also used as the data-test suffix)
   * @param label the display label
   * @param href the target URL, {@code null} for placeholder items
   * @param active whether this item is the currently displayed page
   */
  public record NavItem(String key, String label, @Nullable String href, boolean active) {

    private NavItem(
        final String key, final String label, final @Nullable String href, final String activeKey) {
      this(key, label, href, key.equals(activeKey));
    }

    public boolean navigable() {
      return href != null;
    }
  }
}
