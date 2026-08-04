package de.sample.aiarchitecture.account.adapter.incoming.web;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Builds the login redirect shared by every page of the account area.
 *
 * <p>Anonymous visitors, and identities whose account is missing or cannot log in, are redirected
 * to the login page with a {@code returnUrl} pointing back to the page they requested.
 */
final class AccountLoginRedirect {

  private AccountLoginRedirect() {}

  /**
   * Builds the login redirect view name for the given page path.
   *
   * @param path the path to return to after a successful login
   * @return the view name {@code redirect:/login?returnUrl=<encoded path>}
   */
  static String toLoginWithReturnUrl(final String path) {
    return "redirect:/login?returnUrl=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
  }
}
