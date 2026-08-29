package dev.domaincentric.sample.ecommerce.checkout.application.confirmcheckout;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.DomainEventPublisher;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutArticleDataPort;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutSessionRepository;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutArticle;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutArticlePriceResolver;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSession;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSessionId;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for confirming a checkout session.
 *
 * <p>This use case handles the confirmation step by:
 *
 * <ul>
 *   <li>Loading and validating the checkout session
 *   <li>Fetching fresh article data (pricing, availability) via CheckoutArticleDataPort
 *   <li>Building a resolver for current pricing validation
 *   <li>Calling the domain method to confirm with validation
 *   <li>Persisting the updated session
 *   <li>Publishing domain events — triggers cart completion and stock reduction in separate
 *       transactions via Interface Inversion pattern
 * </ul>
 *
 * <p><b>One Aggregate Per Transaction:</b> This use case only modifies the {@code CheckoutSession}
 * aggregate. Cart completion and stock reduction happen in separate transactions via event
 * listeners (Interface Inversion pattern), respecting the DDD rule of one aggregate per
 * transaction.
 */
@Service
@Transactional
public class ConfirmCheckoutUseCase implements ConfirmCheckoutInputPort {

  private final CheckoutSessionRepository checkoutSessionRepository;
  private final CheckoutArticleDataPort checkoutArticleDataPort;
  private final DomainEventPublisher domainEventPublisher;

  public ConfirmCheckoutUseCase(
      final CheckoutSessionRepository checkoutSessionRepository,
      final CheckoutArticleDataPort checkoutArticleDataPort,
      final DomainEventPublisher domainEventPublisher) {
    this.checkoutSessionRepository = checkoutSessionRepository;
    this.checkoutArticleDataPort = checkoutArticleDataPort;
    this.domainEventPublisher = domainEventPublisher;
  }

  @Override
  public ConfirmCheckoutResult execute(final ConfirmCheckoutCommand command) {
    // Load session
    final CheckoutSessionId sessionId = CheckoutSessionId.of(command.sessionId());
    final CheckoutSession session =
        checkoutSessionRepository
            .findById(sessionId)
            .orElseThrow(
                () -> new IllegalArgumentException("Session not found: " + command.sessionId()));

    // Collect product IDs from line items
    final List<ProductId> productIds =
        session.lineItems().stream().map(item -> item.productId()).toList();

    // Fetch fresh article data (pricing, availability) for validation
    final Map<ProductId, CheckoutArticle> articleDataMap =
        checkoutArticleDataPort.getArticleData(productIds);

    // Build resolver from fetched data
    final CheckoutArticlePriceResolver resolver =
        productId -> {
          final CheckoutArticle article = articleDataMap.get(productId);
          if (article == null) {
            throw new IllegalArgumentException("Article data not found for: " + productId.value());
          }
          return new CheckoutArticlePriceResolver.ArticlePrice(
              article.currentPrice(), article.isAvailable(), article.availableStock());
        };

    // Confirm checkout with validation (domain validates state, step, completeness, and items)
    // This raises the CheckoutConfirmed integration event
    session.confirm(resolver);

    // Save session
    checkoutSessionRepository.save(session);

    // Publish domain events — triggers cross-module listeners via interface inversion:
    // - CartCompletionEventConsumer completes the cart (separate transaction)
    // - StockReductionEventConsumer reduces stock (separate transaction)
    domainEventPublisher.publishAndClearEvents(session);

    // Map to response
    return mapToResponse(session);
  }

  private ConfirmCheckoutResult mapToResponse(final CheckoutSession session) {
    return new ConfirmCheckoutResult(
        session.id().value().toString(),
        session.currentStep().name(),
        session.status().name(),
        session.cartId().value().toString(),
        session.customerId().value(),
        session.totals().total().amount().toPlainString(),
        session.totals().total().currency().getCurrencyCode(),
        session.orderReference());
  }
}
