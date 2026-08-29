package dev.domaincentric.sample.ecommerce.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.domaincentric.sample.ecommerce.account.application.getaccountoverview.GetAccountOverviewResult.AccountOverview;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Rendered-markup tests for {@code templates/account/overview.pug}.
 *
 * <p>Pins that the overview page renders the profile and change-password nav items as real links
 * while orders stays a disabled placeholder, and that it marks its own item as the current page.
 */
@DisplayName("account/overview.pug")
class AccountOverviewTemplateTest {

  private static Document render() {
    return PugTemplateRenderer.render(
        MyAccountPageController.VIEW_NAME,
        Map.of(
            "title",
            "My Account",
            MyAccountPageController.MODEL_ATTRIBUTE,
            MyAccountPageViewModel.from(new AccountOverview("jane.doe@example.com", null))));
  }

  @Test
  @DisplayName("renders the change-password item as a link to /account/change-password")
  void rendersChangePasswordAsLink() {
    final Element item = element(render(), "account-nav-change-password");

    assertEquals("a", item.tagName(), "the change-password item must be a real link");
    assertEquals("/account/change-password", item.attr("href"));
    assertEquals("Change Password", item.text());
  }

  @Test
  @DisplayName("renders the profile item as a link to /account/profile")
  void rendersProfileAsLink() {
    final Element item = element(render(), "account-nav-profile");

    assertEquals("a", item.tagName(), "the profile item must be a real link");
    assertEquals("/account/profile", item.attr("href"));
    assertEquals("My Profile", item.text());
  }

  @Test
  @DisplayName("still renders orders as a disabled span")
  void stillRendersOrdersAsDisabledSpan() {
    final Element item = element(render(), "account-nav-orders");

    assertEquals("span", item.tagName(), "the orders placeholder must stay a span");
    assertEquals(
        "true", item.attr("aria-disabled"), "the orders placeholder must be aria-disabled");
  }

  @Test
  @DisplayName("marks the overview item, and only it, as the current page")
  void marksOverviewItemAsCurrentPage() {
    final Document document = render();

    assertEquals(
        List.of("account-nav-overview"),
        document.select("[data-test^=account-nav-][aria-current=page]").stream()
            .map(item -> item.attr("data-test"))
            .toList(),
        "the overview page marks exactly its own navigation item as the current page");
  }

  private static Element element(final Document document, final String dataTest) {
    final Element element = document.selectFirst("[data-test=" + dataTest + "]");
    assertNotNull(element, "missing element with data-test=\"" + dataTest + "\"");
    return element;
  }
}
