package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import dev.domaincentric.sample.ecommerce.account.adapter.incoming.web.AccountNavigation.NavItem;
import dev.domaincentric.sample.ecommerce.account.application.getaccountoverview.GetAccountOverviewResult.AccountOverview;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * ViewModel for the "My Account" overview page.
 *
 * <p>Carries the account details rendered on the overview page and the left navigation items. The
 * greeting wording is composed in the template, like every other piece of layout wording.
 *
 * @param email the account's email address
 * @param lastLoginDisplay the formatted last login timestamp for display
 * @param navItems the left navigation items in display order
 */
public record MyAccountPageViewModel(
    String email, String lastLoginDisplay, List<NavItem> navItems) {

  public static final String NEVER_LOGGED_IN = "Never";

  private static final DateTimeFormatter LAST_LOGIN_FORMATTER =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneOffset.UTC);

  public static MyAccountPageViewModel from(final AccountOverview overview) {
    return new MyAccountPageViewModel(
        overview.email(),
        formatLastLogin(overview.lastLoginAt()),
        AccountNavigation.itemsWithActive(AccountNavigation.OVERVIEW));
  }

  private static String formatLastLogin(final @Nullable Instant lastLoginAt) {
    return lastLoginAt == null ? NEVER_LOGGED_IN : LAST_LOGIN_FORMATTER.format(lastLoginAt);
  }
}
