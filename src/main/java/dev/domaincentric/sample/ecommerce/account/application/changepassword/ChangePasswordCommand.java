package dev.domaincentric.sample.ecommerce.account.application.changepassword;

/**
 * Command for changing the password of the currently authenticated account.
 *
 * <p>Only the {@code userId} is validated here — it identifies the account whose password is
 * changed and is supplied by the incoming adapter from the current identity, never by the user.
 * Both passwords stay unvalidated input: verifying the current one and enforcing the strength rules
 * of the new one are domain decisions, reported through {@link ChangePasswordResult}.
 *
 * @param userId the linked UserId of the account whose password is changed
 * @param currentPassword the current plaintext password, for verification
 * @param newPassword the new plaintext password
 */
public record ChangePasswordCommand(String userId, String currentPassword, String newPassword) {

  public ChangePasswordCommand {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("User ID is required");
    }
  }
}
