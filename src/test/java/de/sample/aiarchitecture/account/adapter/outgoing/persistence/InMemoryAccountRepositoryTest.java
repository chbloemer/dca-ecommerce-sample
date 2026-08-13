package de.sample.aiarchitecture.account.adapter.outgoing.persistence;

import de.sample.aiarchitecture.account.application.shared.AccountRepository;
import org.junit.jupiter.api.DisplayName;

/** Runs the {@link AccountRepositoryContractTest} against the in-memory adapter. */
@DisplayName("InMemoryAccountRepository")
class InMemoryAccountRepositoryTest extends AccountRepositoryContractTest {

  @Override
  AccountRepository createRepository() {
    return new InMemoryAccountRepository();
  }
}
