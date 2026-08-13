package de.sample.aiarchitecture.account.adapter.incoming.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.sample.aiarchitecture.account.adapter.incoming.web.AccountNavigation.NavItem;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link ProfilePageViewModel}.
 *
 * <p>Pins the pre-filled field values, the date of birth rendered in the format the date input
 * expects, the navigation marking the profile item active, and the field values surviving an error
 * re-render.
 */
@DisplayName("ProfilePageViewModel")
class ProfilePageViewModelTest {

  private static final String FULL_NAME = "Jane Doe";
  private static final String EMAIL = "jane.doe@example.com";
  private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1990, 5, 17);

  @Test
  @DisplayName("carries the stored name, email and date of birth as the displayed values")
  void carriesStoredValues() {
    final ProfilePageViewModel viewModel = ProfilePageViewModel.of(FULL_NAME, EMAIL, DATE_OF_BIRTH);

    assertEquals(FULL_NAME, viewModel.fullName());
    assertEquals(EMAIL, viewModel.email());
    assertEquals("1990-05-17", viewModel.dateOfBirth());
  }

  @Test
  @DisplayName("renders the date of birth in the ISO format the date input expects")
  void rendersDateOfBirthAsIsoDate() {
    assertEquals(
        "2001-12-03",
        ProfilePageViewModel.of(FULL_NAME, EMAIL, LocalDate.of(2001, 12, 3)).dateOfBirth(),
        "a date input pre-fills only from an ISO date");
  }

  @Test
  @DisplayName("marks the profile item as the active navigation item")
  void marksProfileItemActive() {
    final List<NavItem> navItems =
        ProfilePageViewModel.of(FULL_NAME, EMAIL, DATE_OF_BIRTH).navItems();

    assertEquals(
        List.of(AccountNavigation.PROFILE),
        navItems.stream().filter(NavItem::active).map(NavItem::key).toList(),
        "the profile page marks exactly its own navigation item active");
  }

  @Test
  @DisplayName("renders the same four navigation items as every other account page")
  void rendersTheAccountSiteMap() {
    assertEquals(
        AccountNavigation.itemsWithActive(AccountNavigation.PROFILE),
        ProfilePageViewModel.of(FULL_NAME, EMAIL, DATE_OF_BIRTH).navItems());
  }

  @Test
  @DisplayName("carries no message without a success or error")
  void carriesNoMessageByDefault() {
    final ProfilePageViewModel viewModel = ProfilePageViewModel.of(FULL_NAME, EMAIL, DATE_OF_BIRTH);

    assertNull(viewModel.successMessage());
    assertNull(viewModel.errorMessage());
  }

  @Test
  @DisplayName("a success carries the message and keeps the field values")
  void successCarriesMessageAndValues() {
    final ProfilePageViewModel viewModel =
        ProfilePageViewModel.withSuccess(
            FULL_NAME, EMAIL, DATE_OF_BIRTH, "Your profile has been updated.");

    assertEquals("Your profile has been updated.", viewModel.successMessage());
    assertNull(viewModel.errorMessage());
    assertEquals(EMAIL, viewModel.email());
    assertEquals("1990-05-17", viewModel.dateOfBirth());
  }

  @Test
  @DisplayName("an error keeps the submitted field values so they are not retyped")
  void errorKeepsSubmittedValues() {
    final ProfilePageViewModel viewModel =
        ProfilePageViewModel.withError(
            FULL_NAME,
            "taken@example.com",
            "1985-01-02",
            "This email address is already registered");

    assertEquals("This email address is already registered", viewModel.errorMessage());
    assertNull(viewModel.successMessage());
    assertEquals("taken@example.com", viewModel.email());
    assertEquals("1985-01-02", viewModel.dateOfBirth());
  }

  @Test
  @DisplayName("an error keeps a date value the domain could not read at all")
  void errorKeepsUnreadableDate() {
    final ProfilePageViewModel viewModel =
        ProfilePageViewModel.withError(FULL_NAME, EMAIL, "not-a-date", "Please enter a date");

    assertEquals(
        "not-a-date",
        viewModel.dateOfBirth(),
        "a submission rejected for its date must still show what was typed");
  }

  @Test
  @DisplayName("an error still renders the stored name, which was never submitted")
  void errorKeepsStoredName() {
    final ProfilePageViewModel viewModel =
        ProfilePageViewModel.withError(FULL_NAME, "taken@example.com", "1990-05-17", "rejected");

    assertEquals(
        FULL_NAME,
        viewModel.fullName(),
        "the name has no input, so it must come from the stored profile");
  }
}
