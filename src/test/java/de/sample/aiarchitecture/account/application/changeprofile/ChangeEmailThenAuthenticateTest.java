package de.sample.aiarchitecture.account.application.changeprofile;

import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.DATE_OF_BIRTH;
import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.EMAIL;
import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.NEW_EMAIL;
import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.PASSWORD;
import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.USER_ID;
import static de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.accountWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.sample.aiarchitecture.account.application.authenticateaccount.AuthenticateAccountCommand;
import de.sample.aiarchitecture.account.application.authenticateaccount.AuthenticateAccountResult;
import de.sample.aiarchitecture.account.application.authenticateaccount.AuthenticateAccountUseCase;
import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileResult.Outcome;
import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.TestAccountRepository;
import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.TestDomainEventPublisher;
import de.sample.aiarchitecture.account.application.changeprofile.ChangeProfileTestFixtures.TestPasswordHasher;
import de.sample.aiarchitecture.account.domain.model.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Use case collaboration test: after the email was changed, the new address logs in with the
 * unchanged password and the old address no longer does.
 *
 * <p>The email is the login credential, so a change to it must move the credential with it.
 */
@DisplayName("Change email, then authenticate")
class ChangeEmailThenAuthenticateTest {

  private TestAccountRepository accountRepository;
  private ChangeProfileUseCase changeProfile;
  private AuthenticateAccountUseCase authenticate;

  @BeforeEach
  void setUp() {
    final TestPasswordHasher passwordHasher = new TestPasswordHasher();
    accountRepository = new TestAccountRepository();
    changeProfile = new ChangeProfileUseCase(accountRepository, new TestDomainEventPublisher());
    authenticate =
        new AuthenticateAccountUseCase(accountRepository, passwordHasher);

    accountRepository.store(accountWith(AccountStatus.ACTIVE, passwordHasher));
  }

  @Test
  @DisplayName("the new email authenticates with the unchanged password, the old one does not")
  void newEmailAuthenticatesAfterChange() {
    assertEquals(
        Outcome.CHANGED,
        changeProfile
            .execute(new ChangeProfileCommand(USER_ID, NEW_EMAIL, DATE_OF_BIRTH))
            .outcome(),
        "precondition: the email change must succeed");

    final AuthenticateAccountResult withNewEmail =
        authenticate.execute(new AuthenticateAccountCommand(NEW_EMAIL, PASSWORD));
    assertTrue(withNewEmail.success(), "the new email must authenticate");

    final AuthenticateAccountResult withOldEmail =
        authenticate.execute(new AuthenticateAccountCommand(EMAIL, PASSWORD));
    assertFalse(withOldEmail.success(), "the superseded email must no longer authenticate");
  }
}
