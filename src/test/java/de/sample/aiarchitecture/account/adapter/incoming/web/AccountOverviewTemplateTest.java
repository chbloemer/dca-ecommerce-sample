package de.sample.aiarchitecture.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.sample.aiarchitecture.account.application.getaccountoverview.GetAccountOverviewResult.AccountOverview;
import java.util.List;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Rendered-markup tests for {@code templates/account/overview.pug}.
 *
 * <p>Pins that the overview page renders the change-password nav item as a real link while profile
 * and orders stay disabled placeholders.
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
  @DisplayName("still renders profile and orders as disabled spans")
  void stillRendersPlaceholdersAsDisabledSpans() {
    final Document document = render();

    for (final String key : List.of("profile", "orders")) {
      final Element item = element(document, "account-nav-" + key);
      assertEquals("span", item.tagName(), "placeholder '" + key + "' must stay a span");
      assertEquals(
          "true", item.attr("aria-disabled"), "placeholder '" + key + "' must be aria-disabled");
    }
  }

  private static Element element(final Document document, final String dataTest) {
    final Element element = document.selectFirst("[data-test=" + dataTest + "]");
    assertNotNull(element, "missing element with data-test=\"" + dataTest + "\"");
    return element;
  }
}
