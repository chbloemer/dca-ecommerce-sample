package dev.domaincentric.sample.ecommerce.account.application.changeprofile;

import java.time.LocalDate;

/**
 * Command for changing the basic profile information of the currently authenticated account.
 *
 * <p>The owner's name is deliberately not part of this command: it is captured once at registration
 * and the profile page must not offer changing it. Only the date of birth of the same owner can be
 * corrected here.
 *
 * @param userId the linked user ID of the current identity
 * @param email the submitted email address
 * @param dateOfBirth the submitted date of birth of the account's owner
 */
public record ChangeProfileCommand(String userId, String email, LocalDate dateOfBirth) {}
