/**
 * Outgoing adapter for the transaction boundary.
 *
 * <p>{@link
 * dev.domaincentric.sample.ecommerce.sharedkernel.adapter.outgoing.transaction.SpringUnitOfWork}
 * implements the {@code UnitOfWork} output port with Spring's {@code TransactionTemplate}. Use
 * cases that also call remote-capable ports use it to keep the transaction short.
 */
@org.jspecify.annotations.NullMarked
package dev.domaincentric.sample.ecommerce.sharedkernel.adapter.outgoing.transaction;
