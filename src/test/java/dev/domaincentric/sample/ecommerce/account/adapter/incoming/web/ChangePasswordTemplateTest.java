package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.domaincentric.sample.ecommerce.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Rendered-markup tests for {@code templates/account/change-password.pug}.
 *
 * <p>Covers the page root, the navigation, the heading, the card, the form and its three fields,
 * and the error and success message elements.
 *
 * <p>These tests render the production template through {@code layout.pug} and assert on the parsed
 * DOM — they are the only tests that pin this page's {@code data-test} contract, which a later E2E
 * page object binds to.
 */
@DisplayName("account/change-password.pug")
class ChangePasswordTemplateTest {

  private static final String HINT_TEXT =
      "Password must be at least 8 characters and contain an uppercase letter,"
          + " a lowercase letter and a digit";

  private static Document render(final ChangePasswordPageViewModel viewModel) {
    return PugTemplateRenderer.render(
        ChangePasswordPageController.VIEW_NAME,
        Map.of(
            "title", "Change Password", ChangePasswordPageController.MODEL_ATTRIBUTE, viewModel));
  }

  private static Document renderBlank() {
    return render(ChangePasswordPageViewModel.blank());
  }

  @Test
  @DisplayName("renders a root element with data-test=change-password-page")
  void rendersPageRoot() {
    assertNotNull(
        renderBlank().selectFirst("[data-test=change-password-page]"),
        "the page must have a root element carrying data-test=\"change-password-page\"");
  }

  @Test
  @DisplayName("renders the account navigation with one element per nav item")
  void rendersAccountNavigation() {
    final Document document = renderBlank();

    assertNotNull(
        document.selectFirst("[data-test=account-nav]"),
        "the change password page must render the account left navigation");

    for (final NavItem navItem :
        AccountNavigation.itemsWithActive(AccountNavigation.CHANGE_PASSWORD)) {
      final Element item = element(document, "account-nav-" + navItem.key());
      if (navItem.navigable()) {
        assertEquals("a", item.tagName(), "navigable item '" + navItem.key() + "' must be an <a>");
        assertEquals(navItem.href(), item.attr("href"));
      } else {
        assertEquals(
            "span", item.tagName(), "non-navigable item '" + navItem.key() + "' must be a <span>");
        assertEquals(
            "true",
            item.attr("aria-disabled"),
            "non-navigable item '" + navItem.key() + "' must be aria-disabled");
      }
    }
  }

  @Test
  @DisplayName("the active navigation item renders aria-current=page")
  void activeNavItemRendersAriaCurrent() {
    final Document document = renderBlank();

    assertEquals(
        "page",
        element(document, "account-nav-change-password").attr("aria-current"),
        "the change-password item is the active item on this page");
    assertTrue(
        document.select("[data-test^=account-nav-][aria-current=page]").size() == 1,
        "exactly one navigation item may be marked as the current page");
  }

  @Test
  @DisplayName("renders the heading 'Change Password'")
  void rendersHeading() {
    assertEquals("Change Password", element(renderBlank(), "change-password-title").text());
  }

  @Test
  @DisplayName("renders no card header")
  void rendersNoCardHeader() {
    assertTrue(
        element(renderBlank(), "change-password-page").select(".card__header").isEmpty(),
        "the change password card carries the form as its body, without a card header");
  }

  @Test
  @DisplayName("renders a POST form to /account/change-password")
  void rendersForm() {
    final Element form = element(renderBlank(), "change-password-form");

    assertEquals("form", form.tagName());
    assertEquals("POST", form.attr("method").toUpperCase(java.util.Locale.ROOT));
    assertEquals("/account/change-password", form.attr("action"));
  }

  @Test
  @DisplayName("first field is the required, autofocused current password input")
  void rendersCurrentPasswordField() {
    final Document document = renderBlank();
    final Element input = element(document, "change-password-current-password-input");

    assertEquals("input", input.tagName());
    assertEquals("password", input.attr("type"));
    assertEquals("currentPassword", input.attr("name"));
    assertEquals("currentPassword", input.id());
    assertTrue(input.hasAttr("required"), "the current password is required");
    assertTrue(input.hasAttr("autofocus"), "the current password field receives the focus");
    assertEquals("Current Password", labelFor(document, "currentPassword").text());
  }

  @Test
  @DisplayName("second field is the required new password input with minlength 8")
  void rendersNewPasswordField() {
    final Document document = renderBlank();
    final Element input = element(document, "change-password-new-password-input");

    assertEquals("input", input.tagName());
    assertEquals("password", input.attr("type"));
    assertEquals("newPassword", input.attr("name"));
    assertEquals("newPassword", input.id());
    assertTrue(input.hasAttr("required"), "the new password is required");
    assertEquals("8", input.attr("minlength"));
    assertEquals("New Password", labelFor(document, "newPassword").text());
  }

  @Test
  @DisplayName("the new password field is followed by the password rules hint")
  void rendersPasswordRulesHint() {
    final Document document = renderBlank();
    final Element hint =
        document.getAllElements().stream()
            .filter(element -> HINT_TEXT.equals(element.ownText()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Missing password rules hint: " + HINT_TEXT));

    assertTrue(
        position(document, hint)
            > position(document, element(document, "change-password-new-password-input")),
        "the hint must follow the new password field");
    assertTrue(
        position(document, hint)
            < position(document, element(document, "change-password-confirm-password-input")),
        "the hint must precede the confirmation field");
  }

  @Test
  @DisplayName("third field is the required confirm new password input")
  void rendersConfirmPasswordField() {
    final Document document = renderBlank();
    final Element input = element(document, "change-password-confirm-password-input");

    assertEquals("input", input.tagName());
    assertEquals("password", input.attr("type"));
    assertEquals("confirmPassword", input.attr("name"));
    assertEquals("confirmPassword", input.id());
    assertTrue(input.hasAttr("required"), "the confirmation is required");
    assertEquals("Confirm New Password", labelFor(document, "confirmPassword").text());
  }

  @Test
  @DisplayName("the three fields appear in order current, new, confirm")
  void rendersFieldsInOrder() {
    final Document document = renderBlank();

    assertEquals(
        List.of(
            "change-password-current-password-input",
            "change-password-new-password-input",
            "change-password-confirm-password-input"),
        document.select("[data-test^=change-password-][type=password]").stream()
            .map(input -> input.attr("data-test"))
            .toList());
  }

  @Test
  @DisplayName("renders the submit button 'Change Password'")
  void rendersSubmitButton() {
    final Element button = element(renderBlank(), "change-password-submit-button");

    assertEquals("submit", button.attr("type"));
    assertEquals("Change Password", button.text());
  }

  @Test
  @DisplayName("no password input renders a value attribute")
  void rendersNoPasswordValues() {
    for (final Element input : renderBlank().select("input[type=password]")) {
      assertFalse(
          input.hasAttr("value"),
          "password input '" + input.attr("name") + "' must never be pre-filled");
    }
  }

  @Test
  @DisplayName("renders the error message when the ViewModel carries one")
  void rendersErrorMessage() {
    final Document document =
        render(ChangePasswordPageViewModel.withError("Current password is not correct"));

    assertEquals(
        "Current password is not correct",
        element(document, "change-password-error-message").text());
  }

  @Test
  @DisplayName("omits the error message element when the ViewModel carries none")
  void omitsErrorMessageElement() {
    assertNull(
        renderBlank().selectFirst("[data-test=change-password-error-message]"),
        "no error element may be rendered without an error message");
  }

  @Test
  @DisplayName("renders the success message when the ViewModel carries one")
  void rendersSuccessMessage() {
    final Document document =
        render(ChangePasswordPageViewModel.withSuccess("Your password has been changed."));

    assertEquals(
        "Your password has been changed.",
        element(document, "change-password-success-message").text());
  }

  @Test
  @DisplayName("omits the success message element when the ViewModel carries none")
  void omitsSuccessMessageElement() {
    assertNull(
        renderBlank().selectFirst("[data-test=change-password-success-message]"),
        "no success element may be rendered without a success message");
  }

  private static Element labelFor(final Document document, final String inputId) {
    final Element label = document.selectFirst("label[for=" + inputId + "]");
    assertNotNull(label, "missing label for input id '" + inputId + "'");
    return label;
  }

  private static Element element(final Document document, final String dataTest) {
    final Element element = document.selectFirst("[data-test=" + dataTest + "]");
    assertNotNull(element, "missing element with data-test=\"" + dataTest + "\"");
    return element;
  }

  private static int position(final Document document, final Element element) {
    return document.getAllElements().indexOf(element);
  }
}
