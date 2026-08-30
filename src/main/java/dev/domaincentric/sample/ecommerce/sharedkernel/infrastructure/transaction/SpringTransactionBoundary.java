package dev.domaincentric.sample.ecommerce.sharedkernel.infrastructure.transaction;

import dev.domaincentric.dca.buildingblocks.application.TransactionBoundary;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Binds {@link TransactionBoundary} to Spring's transaction manager.
 *
 * <p>Use cases that talk to other contexts or external systems draw their transaction boundary by
 * hand with this application-layer abstraction (not a port) instead of a class-level
 * {@code @Transactional}: remote reads happen before {@link #inTransaction(Supplier)}, the
 * transactional core (load, mutate, save, publish) inside it. Domain events published inside share
 * the transaction; after-commit listeners fire on commit.
 */
@Component
public class SpringTransactionBoundary implements TransactionBoundary {

  private final TransactionTemplate transactionTemplate;

  public SpringTransactionBoundary(final PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public <T> T inTransaction(final Supplier<T> work) {
    return transactionTemplate.execute(status -> work.get());
  }
}
