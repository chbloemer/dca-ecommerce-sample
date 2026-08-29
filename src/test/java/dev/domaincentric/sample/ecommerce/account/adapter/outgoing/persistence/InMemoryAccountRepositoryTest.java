package dev.domaincentric.sample.ecommerce.account.adapter.outgoing.persistence;

import dev.domaincentric.sample.ecommerce.account.application.shared.AccountRepository;
import org.junit.jupiter.api.DisplayName;

/** Runs the {@link AccountRepositoryContractTest} against the in-memory adapter. */
@DisplayName("InMemoryAccountRepository")
class InMemoryAccountRepositoryTest extends AccountRepositoryContractTest {

  @Override
  AccountRepository createRepository() {
    return new InMemoryAccountRepository();
  }
}
