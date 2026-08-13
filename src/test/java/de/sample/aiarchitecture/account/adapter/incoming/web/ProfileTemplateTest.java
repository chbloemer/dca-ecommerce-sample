package de.sample.aiarchitecture.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sample.aiarchitecture.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Rendered-markup tests for {@code templates/account/profile.pug}.
 *
 * <p>Covers the page root, the navigation, the heading and browser title, the name shown as text
 * with the note explaining that it cannot be changed, the form with exactly two editable fields,
 * the pre-filled values, and the error and success message elements.
 *
 * <p>These tests render the production template through {@code layout.pug} and assert on the parsed
 * DOM — they are the only tests that pin this page's {@code data-test} contract, which a later E2E
 * page object binds to.
 */
@DisplayName("account/profile.pug")
class ProfileTemplateTest {

  private static final String FULL_NAME = "Jane Doe";
  private static final String EMAIL = "jane.doe@example.com";
  private static final String DATE_OF_BIRTH = "1990-05-17";

  private static final String NAME_NOTE =
      "Your name was set when you created your account and cannot be changed.";

  /** Anything a user could read as an input for their name. */
  private static final Pattern NAME_FIELD =
      Pattern.compile(".*(first ?name|last ?name|full ?name|surname|firstname|lastname).*");

  private static Document render(final ProfilePageViewModel viewModel) {
    return PugTemplateRenderer.render(
        ProfilePageController.VIEW_NAME,
        Map.of("title", "My Profile", ProfilePageController.MODEL_ATTRIBUTE, viewModel));
  }

  private static ProfilePageViewModel viewModel(
      final String email,
      final String dateOfBirth,
      final String successMessage,
      final String errorMessage) {
    return new ProfilePageViewModel(
        FULL_NAME,
        email,
        dateOfBirth,
        AccountNavigation.itemsWithActive(AccountNavigation.PROFILE),
        successMessage,
        errorMessage);
  }

  private static Document renderStored() {
    return render(viewModel(EMAIL, DATE_OF_BIRTH, null, null));
  }

  @Test
  @DisplayName("renders a root element with data-test=profile-page")
  void rendersPageRoot() {
    assertNotNull(
        renderStored().selectFirst("[data-test=profile-page]"),
        "the page must have a root element carrying data-test=\"profile-page\"");
  }

  @Test
  @DisplayName("renders the account navigation with one element per nav item")
  void rendersAccountNavigation() {
    final Document document = renderStored();

    assertNotNull(
        document.selectFirst("[data-test=account-nav]"),
        "the profile page must render the account left navigation");

    for (final NavItem navItem : AccountNavigation.itemsWithActive(AccountNavigation.PROFILE)) {
      final Element item = element(document, "account-nav-" + navItem.key());
      if (navItem.navigable()) {
        assertEquals("a", item.tagName(), "navigable item '" + navItem.key() + "' must be an <a>");
        assertEquals(navItem.href(), item.attr("href"));
      } else {
        assertEquals(
            "span", item.tagName(), "non-navigable item '" + navItem.key() + "' must be a <span>");
      }
    }
  }

  @Test
  @DisplayName("the profile item is the active navigation item, rendered as a link")
  void profileItemIsActive() {
    final Document document = renderStored();
    final Element item = element(document, "account-nav-profile");

    assertEquals("a", item.tagName(), "the profile item links to the page the user is on");
    assertEquals("/account/profile", item.attr("href"));
    assertEquals("page", item.attr("aria-current"));
    assertTrue(
        item.hasClass("account__nav-link--active"),
        "the active item must carry the active modifier class");
    assertEquals(
        1,
        document.select("[data-test^=account-nav-][aria-current=page]").size(),
        "exactly one navigation item may be marked as the current page");
  }

  @Test
  @DisplayName("renders the heading 'My Profile'")
  void rendersHeading() {
    assertEquals("My Profile", element(renderStored(), "profile-title").text());
  }

  @Test
  @DisplayName("renders the browser page title 'My Profile'")
  void rendersBrowserTitle() {
    assertEquals("My Profile", renderStored().title());
  }

  // ---------------------------------------------------------------- the name, read-only

  @Test
  @DisplayName("displays the owner's name as text")
  void displaysOwnerName() {
    assertEquals(FULL_NAME, element(renderStored(), "profile-owner-name").text());
  }

  @Test
  @DisplayName("displays the name outside the form, so it is never submitted")
  void displaysNameOutsideTheForm() {
    final Document document = renderStored();

    assertNull(
        element(document, "profile-owner-name").closest("form"),
        "a name inside the form could be submitted; it must sit outside it");
  }

  @Test
  @DisplayName("states inside the card, above the form, that the name cannot be changed")
  void rendersNameNote() {
    final Document document = renderStored();
    final Element note = element(document, "profile-name-note");

    assertEquals(NAME_NOTE, note.text());
    assertNotNull(note.closest(".card"), "the note belongs inside the card that carries the form");
    assertTrue(
        position(document, note) < position(document, element(document, "profile-form")),
        "the note must precede the form");
  }

  @Test
  @DisplayName("renders no input, select or textarea for a first name, last name or surname")
  void rendersNoNameField() {
    final Document document = renderStored();

    for (final Element field :
        element(document, "profile-page").select("input, select, textarea")) {
      for (final String candidate :
          List.of(
              field.attr("name"),
              field.id(),
              field.attr("placeholder"),
              field.attr("aria-label"),
              labelTextFor(document, field.id()))) {
        assertFalse(
            NAME_FIELD.matcher(candidate.toLowerCase(Locale.ROOT)).matches(),
            "the profile page must offer no name field, found: " + field.outerHtml());
      }
    }
  }

  // ---------------------------------------------------------------- the editable form

  @Test
  @DisplayName("renders a POST form to /account/profile")
  void rendersForm() {
    final Element form = element(renderStored(), "profile-form");

    assertEquals("form", form.tagName());
    assertEquals("POST", form.attr("method").toUpperCase(Locale.ROOT));
    assertEquals("/account/profile", form.attr("action"));
  }

  @Test
  @DisplayName("the form has exactly two editable fields, email first and date of birth second")
  void rendersExactlyTwoFieldsInOrder() {
    final Document document = renderStored();

    assertEquals(
        List.of("profile-email-input", "profile-date-of-birth-input"),
        element(document, "profile-form")
            .select("input:not([type=hidden]), select, textarea")
            .stream()
            .map(field -> field.attr("data-test"))
            .toList(),
        "the profile form offers exactly the email and the date of birth, in that order");
  }

  @Test
  @DisplayName("the email field is a required, labelled email input")
  void rendersEmailField() {
    final Document document = renderStored();
    final Element input = element(document, "profile-email-input");

    assertEquals("input", input.tagName());
    assertEquals("email", input.attr("type"));
    assertEquals("email", input.attr("name"));
    assertEquals("email", input.id());
    assertTrue(input.hasAttr("required"), "the email is required");
    assertEquals("Email", labelFor(document, "email").text());
  }

  @Test
  @DisplayName("the date of birth field is a required, labelled date input")
  void rendersDateOfBirthField() {
    final Document document = renderStored();
    final Element input = element(document, "profile-date-of-birth-input");

    assertEquals("input", input.tagName());
    assertEquals(
        "date", input.attr("type"), "a date input keeps the submitted value ISO-formatted");
    assertEquals("dateOfBirth", input.attr("name"));
    assertEquals("dateOfBirth", input.id());
    assertTrue(input.hasAttr("required"), "the date of birth is required");
    assertEquals("Date of Birth", labelFor(document, "dateOfBirth").text());
  }

  @Test
  @DisplayName("pre-fills email and date of birth with the stored values")
  void preFillsStoredValues() {
    final Document document = renderStored();

    assertEquals(EMAIL, element(document, "profile-email-input").attr("value"));
    assertEquals(DATE_OF_BIRTH, element(document, "profile-date-of-birth-input").attr("value"));
  }

  @Test
  @DisplayName("renders the submit button 'Save Changes'")
  void rendersSubmitButton() {
    final Element button = element(renderStored(), "profile-submit-button");

    assertEquals("submit", button.attr("type"));
    assertEquals("Save Changes", button.text());
  }

  @Test
  @DisplayName("renders none of the German labels an earlier draft used")
  void rendersNoGermanFormLabels() {
    final String text = element(renderStored(), "profile-page").text();

    for (final String german :
        List.of("Ändern", "ändern", "Telefon", "Speichern", "E-Mail-Adresse", "Geburtsdatum")) {
      assertFalse(
          text.contains(german), "the page must not fall back to German wording, found: " + german);
    }
  }

  // ---------------------------------------------------------------- messages

  @Test
  @DisplayName("renders the success message when the ViewModel carries one")
  void rendersSuccessMessage() {
    final Document document =
        render(viewModel(EMAIL, DATE_OF_BIRTH, "Your profile has been updated.", null));

    assertEquals(
        "Your profile has been updated.", element(document, "profile-success-message").text());
  }

  @Test
  @DisplayName("omits the success message element when the ViewModel carries none")
  void omitsSuccessMessageElement() {
    assertNull(
        renderStored().selectFirst("[data-test=profile-success-message]"),
        "no success element may be rendered without a success message");
  }

  @Test
  @DisplayName("renders the error message when the ViewModel carries one")
  void rendersErrorMessage() {
    final Document document =
        render(viewModel(EMAIL, DATE_OF_BIRTH, null, "This email address is already registered"));

    assertEquals(
        "This email address is already registered",
        element(document, "profile-error-message").text());
  }

  @Test
  @DisplayName("omits the error message element when the ViewModel carries none")
  void omitsErrorMessageElement() {
    assertNull(
        renderStored().selectFirst("[data-test=profile-error-message]"),
        "no error element may be rendered without an error message");
  }

  private static String labelTextFor(final Document document, final String inputId) {
    if (inputId.isEmpty()) {
      return "";
    }
    final Element label = document.selectFirst("label[for=" + inputId + "]");
    return label == null ? "" : label.text();
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
