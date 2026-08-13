package de.sample.aiarchitecture.account.application.getprofile;

/**
 * Query for reading the profile of the currently authenticated account.
 *
 * @param userId the linked user ID of the current identity
 */
public record GetProfileQuery(String userId) {

  public GetProfileQuery {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("UserId is required");
    }
  }
}
