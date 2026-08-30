package dev.domaincentric.sample.ecommerce.checkout.application.submitpayment;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.DomainEventPublisher;
import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.UnitOfWork;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutSessionRepository;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.PaymentProvider;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.PaymentProviderRegistry;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSession;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.PaymentProviderId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.PaymentSelection;
import org.springframework.stereotype.Service;

/**
 * Use case for submitting payment information during checkout.
 *
 * <p>This use case handles the payment step by:
 *
 * <ul>
 *   <li>Loading and validating the checkout session
 *   <li>Validating the payment provider exists and is available
 *   <li>Creating PaymentSelection value object from command data
 *   <li>Calling the domain method to submit payment info
 *   <li>Persisting the updated session
 * </ul>
 *
 * <p><b>Hexagonal Architecture:</b> This class implements the {@link SubmitPaymentInputPort}
 * interface, which is a primary/driving port in the application layer.
 */
@Service
public class SubmitPaymentUseCase implements SubmitPaymentInputPort {

  private final CheckoutSessionRepository checkoutSessionRepository;
  private final PaymentProviderRegistry paymentProviderRegistry;
  private final DomainEventPublisher eventPublisher;
  private final UnitOfWork unitOfWork;

  public SubmitPaymentUseCase(
      final CheckoutSessionRepository checkoutSessionRepository,
      final PaymentProviderRegistry paymentProviderRegistry,
      final DomainEventPublisher eventPublisher,
      final UnitOfWork unitOfWork) {
    this.checkoutSessionRepository = checkoutSessionRepository;
    this.paymentProviderRegistry = paymentProviderRegistry;
    this.eventPublisher = eventPublisher;
    this.unitOfWork = unitOfWork;
  }

  @Override
  public SubmitPaymentResult execute(final SubmitPaymentCommand command) {
    final CheckoutSessionId sessionId = CheckoutSessionId.of(command.sessionId());
    final PaymentProviderId providerId = PaymentProviderId.of(command.providerId());

    // Provider lookup is remote-capable (payment service provider) - outside the transaction
    final PaymentProvider provider =
        paymentProviderRegistry
            .findById(providerId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Payment provider not found: " + command.providerId()));
    final PaymentSelection paymentSelection =
        PaymentSelection.of(providerId, command.providerReference());

    // Short transaction: load, submit, save, publish
    return unitOfWork.run(
        () -> {
          final CheckoutSession session =
              checkoutSessionRepository
                  .findById(sessionId)
                  .orElseThrow(
                      () ->
                          new IllegalArgumentException(
                              "Session not found: " + command.sessionId()));
          session.submitPayment(paymentSelection);
          checkoutSessionRepository.save(session);
          eventPublisher.publishAndClearEvents(session);
          return mapToResponse(session, provider);
        });
  }

  private SubmitPaymentResult mapToResponse(
      final CheckoutSession session, final PaymentProvider provider) {
    final PaymentSelection payment = session.paymentSelection();
    return new SubmitPaymentResult(
        session.id().value().toString(),
        session.currentStep().name(),
        session.status().name(),
        payment.providerId().value(),
        provider.displayName(),
        payment.providerReference());
  }
}
