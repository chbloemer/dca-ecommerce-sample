package dev.domaincentric.sample.ecommerce.account.application.getaccountoverview;

/**
 * Query for reading the account overview of the currently authenticated user.
 *
 * @param userId the linked UserId of the account to load
 */
public record GetAccountOverviewQuery(String userId) {

  public GetAccountOverviewQuery {
    if (userId == null || userId.isBlank()) {
      throw new IllegalArgumentException("UserId is required");
    }
  }
}
