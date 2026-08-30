package dev.domaincentric.sample.ecommerce.sharedkernel.adapter.outgoing.transaction;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.UnitOfWork;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Binds the {@link UnitOfWork} port to Spring's transaction manager.
 *
 * <p>Use cases that talk to other contexts or external systems draw their transaction boundary by
 * hand with this port instead of a class-level {@code @Transactional}: remote reads happen before
 * {@link #run(Supplier)}, the transactional core (load, mutate, save, publish) inside it. Domain
 * events published inside share the transaction; after-commit listeners fire on commit.
 */
@Component
public class SpringUnitOfWork implements UnitOfWork {

  private final TransactionTemplate transactionTemplate;

  public SpringUnitOfWork(final PlatformTransactionManager transactionManager) {
    this.transactionTemplate = new TransactionTemplate(transactionManager);
  }

  @Override
  public <T> T run(final Supplier<T> work) {
    return transactionTemplate.execute(status -> work.get());
  }
}
