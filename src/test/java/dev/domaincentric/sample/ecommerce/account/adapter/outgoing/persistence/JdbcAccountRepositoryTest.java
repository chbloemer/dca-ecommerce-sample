package dev.domaincentric.sample.ecommerce.account.adapter.outgoing.persistence;

import dev.domaincentric.sample.ecommerce.account.application.shared.AccountRepository;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

/**
 * Runs the {@link AccountRepositoryContractTest} against the JDBC adapter and the production {@code
 * schema.sql}.
 *
 * <p>No Spring context: the adapter takes a {@link DataSource} and nothing else, so booting the
 * application to test it would only test the wiring. The database name is unique to this class so
 * that {@code DB_CLOSE_DELAY=-1} cannot leak rows into another test class.
 */
@DisplayName("JdbcAccountRepository")
class JdbcAccountRepositoryTest extends AccountRepositoryContractTest {

  private static final String URL =
      "jdbc:h2:mem:account-repository-contract;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

  @Override
  AccountRepository createRepository() {
    final DataSource dataSource = new DriverManagerDataSource(URL, "sa", "");
    try (var connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("schema.sql"));
    } catch (final SQLException e) {
      throw new IllegalStateException("Could not prepare the account schema", e);
    }

    // The schema survives between test methods (DB_CLOSE_DELAY=-1), so each one starts from an
    // empty table rather than from whatever the previous one left behind.
    final JdbcTemplate setup = new JdbcTemplate(dataSource);
    setup.execute("DELETE FROM account_roles");
    setup.execute("DELETE FROM accounts");

    return new JdbcAccountRepository(dataSource);
  }
}
