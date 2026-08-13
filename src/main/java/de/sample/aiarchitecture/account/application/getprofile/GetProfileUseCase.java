package de.sample.aiarchitecture.account.application.getprofile;

import de.sample.aiarchitecture.account.application.getprofile.GetProfileResult.Profile;
import de.sample.aiarchitecture.account.application.shared.AccountRepository;
import de.sample.aiarchitecture.account.domain.model.Owner;
import de.sample.aiarchitecture.sharedkernel.domain.model.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for reading the profile of a registered user.
 *
 * <p>Read-only: loads the account linked to the given UserId and projects the fields the profile
 * page renders — the owner's name for display, the email and the date of birth for editing. An
 * account that cannot log in ({@code SUSPENDED}, {@code CLOSED}) is reported as absent, the same
 * rule {@code GetAccountOverviewUseCase} enforces.
 */
@Service
public class GetProfileUseCase implements GetProfileInputPort {

  private final AccountRepository accountRepository;

  public GetProfileUseCase(final AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public GetProfileResult execute(final GetProfileQuery query) {
    return accountRepository
        .findByLinkedUserId(UserId.of(query.userId()))
        .filter(account -> account.status().canLogin())
        .map(account -> profileOf(account.email().value(), account.owner()))
        .map(GetProfileResult::found)
        .orElseGet(GetProfileResult::notFound);
  }

  private static Profile profileOf(final String email, final Owner owner) {
    return new Profile(email, owner.firstName(), owner.lastName(), owner.dateOfBirth());
  }
}
