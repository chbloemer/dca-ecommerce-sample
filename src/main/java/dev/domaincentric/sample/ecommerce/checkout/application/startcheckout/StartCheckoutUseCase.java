package dev.domaincentric.sample.ecommerce.checkout.application.startcheckout;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.DomainEventPublisher;
import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.UnitOfWork;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CartData;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CartDataPort;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutArticleDataPort;
import dev.domaincentric.sample.ecommerce.checkout.application.shared.CheckoutSessionRepository;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutArticle;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutLineItem;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutLineItemId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSession;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Use case for starting a checkout session from a shopping cart.
 *
 * <p>This use case creates a new checkout session by:
 *
 * <ul>
 *   <li>Loading the cart data through the Anti-Corruption Layer
 *   <li>Fetching article data (name, price, availability) via CheckoutArticleDataPort
 *   <li>Creating checkout line items with current product details
 *   <li>Creating and persisting the checkout session
 * </ul>
 *
 * <p><b>Hexagonal Architecture:</b> This class implements the {@link StartCheckoutInputPort}
 * interface, which is a primary/driving port in the application layer.
 *
 * <p><b>Bounded Context Isolation:</b> This use case accesses:
 *
 * <ul>
 *   <li>Cart data through {@link CartDataPort} output port
 *   <li>Article data (name, price, availability) through {@link CheckoutArticleDataPort} output
 *       port
 * </ul>
 *
 * This isolates the Checkout context from direct coupling to other contexts' domain models.
 */
@Service
public class StartCheckoutUseCase implements StartCheckoutInputPort {

  private final CartDataPort cartDataPort;
  private final CheckoutArticleDataPort checkoutArticleDataPort;
  private final CheckoutSessionRepository checkoutSessionRepository;
  private final DomainEventPublisher domainEventPublisher;
  private final UnitOfWork unitOfWork;

  public StartCheckoutUseCase(
      final CartDataPort cartDataPort,
      final CheckoutArticleDataPort checkoutArticleDataPort,
      final CheckoutSessionRepository checkoutSessionRepository,
      final DomainEventPublisher domainEventPublisher,
      final UnitOfWork unitOfWork) {
    this.cartDataPort = cartDataPort;
    this.checkoutArticleDataPort = checkoutArticleDataPort;
    this.checkoutSessionRepository = checkoutSessionRepository;
    this.domainEventPublisher = domainEventPublisher;
    this.unitOfWork = unitOfWork;
  }

  @Override
  public StartCheckoutResult execute(final StartCheckoutCommand command) {
    final CartId cartId = CartId.of(command.cartId());

    // Cart and article data come from other contexts (remote-capable) - outside the transaction
    final CartData cart =
        cartDataPort
            .findById(cartId)
            .orElseThrow(() -> new IllegalArgumentException("Cart not found: " + command.cartId()));
    if (!cart.active()) {
      throw new IllegalArgumentException("Cart is not active: " + command.cartId());
    }
    if (cart.items().isEmpty()) {
      throw new IllegalArgumentException("Cannot checkout empty cart: " + command.cartId());
    }
    final List<ProductId> productIds =
        cart.items().stream().map(CartData.CartItemData::productId).toList();
    final Map<ProductId, CheckoutArticle> articleDataMap =
        checkoutArticleDataPort.getArticleData(productIds);
    final List<CheckoutLineItem> lineItems = new ArrayList<>();
    Money subtotal = Money.euro(0.0);
    for (final CartData.CartItemData cartItem : cart.items()) {
      final CheckoutArticle article = articleDataMap.get(cartItem.productId());
      if (article == null) {
        throw new IllegalArgumentException("Product not found: " + cartItem.productId().value());
      }
      final CheckoutLineItem lineItem =
          CheckoutLineItem.of(
              CheckoutLineItemId.generate(),
              cartItem.productId(),
              article.name(),
              article.currentPrice(),
              cartItem.quantity(),
              article.imageUrl());
      lineItems.add(lineItem);
      subtotal = subtotal.add(lineItem.lineTotal());
    }
    final Money total = subtotal;

    // Short transaction: create, save, publish
    return unitOfWork.run(
        () -> {
          final CheckoutSession session =
              CheckoutSession.start(cart.cartId(), cart.customerId(), lineItems, total);
          checkoutSessionRepository.save(session);
          domainEventPublisher.publishAndClearEvents(session);
          return mapToResult(session);
        });
  }

  private StartCheckoutResult mapToResult(final CheckoutSession session) {
    final List<StartCheckoutResult.LineItemData> lineItemData =
        session.lineItems().stream()
            .map(
                item ->
                    new StartCheckoutResult.LineItemData(
                        item.id().value(),
                        item.productId().value().toString(),
                        item.productName(),
                        item.unitPrice().toString(),
                        item.quantity(),
                        item.lineTotal().toString()))
            .toList();

    return new StartCheckoutResult(
        session.id().value().toString(),
        session.cartId().value(),
        session.customerId().value(),
        session.currentStep().name(),
        session.status().name(),
        lineItemData,
        session.totals().subtotal().toString());
  }
}
