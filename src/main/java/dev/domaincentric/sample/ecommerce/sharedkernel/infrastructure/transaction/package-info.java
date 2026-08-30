/**
 * Infrastructure for the transaction boundary.
 *
 * <p>{@link
 * dev.domaincentric.sample.ecommerce.sharedkernel.infrastructure.transaction.SpringTransactionBoundary}
 * implements {@code TransactionBoundary} (an application-layer execution abstraction, not a port)
 * with Spring's {@code TransactionTemplate}. Use cases that also call remote-capable ports use it
 * to keep the transaction short.
 */
@org.jspecify.annotations.NullMarked
package dev.domaincentric.sample.ecommerce.sharedkernel.infrastructure.transaction;
