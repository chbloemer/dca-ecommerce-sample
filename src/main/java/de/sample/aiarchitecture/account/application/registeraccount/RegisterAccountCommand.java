package de.sample.aiarchitecture.account.application.registeraccount;

import java.time.LocalDate;

/**
 * Command to register a new account.
 *
 * <p>First name, last name and date of birth describe the account's owner. The name is captured
 * here and only here: no later operation changes it, so registration is the one chance to get it
 * right.
 *
 * @param email the user's email address (will be their login credential)
 * @param password the user's plaintext password (will be hashed)
 * @param currentUserId the current user's ID (from their anonymous JWT)
 * @param firstName the owner's first name
 * @param lastName the owner's last name
 * @param dateOfBirth the owner's date of birth
 */
public record RegisterAccountCommand(
    String email,
    String password,
    String currentUserId,
    String firstName,
    String lastName,
    LocalDate dateOfBirth) {

  public RegisterAccountCommand {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email is required");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (currentUserId == null || currentUserId.isBlank()) {
      throw new IllegalArgumentException("Current user ID is required");
    }
  }
}
