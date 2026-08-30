package dev.domaincentric.sample.ecommerce.cart.application.additemtocart;

import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.DomainEventPublisher;
import dev.domaincentric.dca.buildingblocks.hexagonal.port.out.UnitOfWork;
import dev.domaincentric.sample.ecommerce.cart.application.shared.ArticleDataPort;
import dev.domaincentric.sample.ecommerce.cart.application.shared.ShoppingCartRepository;
import dev.domaincentric.sample.ecommerce.cart.domain.model.CartArticle;
import dev.domaincentric.sample.ecommerce.cart.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.cart.domain.model.Quantity;
import dev.domaincentric.sample.ecommerce.cart.domain.model.ShoppingCart;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Price;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Use case for adding an item to a shopping cart.
 *
 * <p>This use case orchestrates adding an item to the cart by:
 *
 * <ol>
 *   <li>Retrieving the cart and article data
 *   <li>Validating business rules (product existence, stock availability)
 *   <li>Adding the item to cart (business logic in aggregate)
 *   <li>Persisting the updated cart
 *   <li>Publishing domain events
 * </ol>
 *
 * <p><b>Hexagonal Architecture:</b> This class implements the {@link AddItemToCartInputPort}
 * interface, which is a primary/driving port in the application layer.
 *
 * <p><b>Bounded Context Isolation:</b> This use case accesses article data through a single output
 * port that aggregates data from multiple contexts:
 *
 * <ul>
 *   <li>{@link ArticleDataPort} - provides name (from Product), price (from Pricing), and stock
 *       (from Inventory) information
 * </ul>
 */
@Service
public class AddItemToCartUseCase implements AddItemToCartInputPort {

  private final ShoppingCartRepository shoppingCartRepository;
  private final ArticleDataPort articleDataPort;
  private final DomainEventPublisher eventPublisher;
  private final UnitOfWork unitOfWork;

  public AddItemToCartUseCase(
      final ShoppingCartRepository shoppingCartRepository,
      final ArticleDataPort articleDataPort,
      final DomainEventPublisher eventPublisher,
      final UnitOfWork unitOfWork) {
    this.shoppingCartRepository = shoppingCartRepository;
    this.articleDataPort = articleDataPort;
    this.eventPublisher = eventPublisher;
    this.unitOfWork = unitOfWork;
  }

  @Override
  public AddItemToCartResult execute(final AddItemToCartCommand input) {
    final CartId cartId = CartId.of(input.cartId());
    final ProductId productId = ProductId.of(input.productId());
    final Quantity quantity = new Quantity(input.quantity());

    // Remote-capable read (Product Catalog via ACL) - outside the transaction
    final CartArticle cartArticle =
        articleDataPort
            .getArticleData(productId)
            .orElseThrow(
                () -> new IllegalArgumentException("Product not found: " + input.productId()));
    if (!cartArticle.hasStockFor(quantity.value())) {
      throw new IllegalArgumentException("Insufficient stock for product: " + input.productId());
    }
    final Price priceAtAddition = Price.of(cartArticle.currentPrice());

    // Short transaction: load, mutate, save, publish
    return unitOfWork.run(
        () -> {
          final ShoppingCart cart =
              shoppingCartRepository
                  .findById(cartId)
                  .orElseThrow(
                      () -> new IllegalArgumentException("Cart not found: " + input.cartId()));
          cart.addItem(productId, quantity, priceAtAddition);
          shoppingCartRepository.save(cart);
          eventPublisher.publishAndClearEvents(cart);
          return toResult(cart);
        });
  }

  private static AddItemToCartResult toResult(final ShoppingCart cart) {
    final List<AddItemToCartResult.CartItemSummary> items =
        cart.items().stream()
            .map(
                item ->
                    new AddItemToCartResult.CartItemSummary(
                        item.id().value().toString(),
                        item.productId().value().toString(),
                        item.quantity().value(),
                        item.priceAtAddition().value().amount(),
                        item.priceAtAddition().value().currency().getCurrencyCode()))
            .toList();
    final Money total = cart.calculateTotal();
    return new AddItemToCartResult(
        cart.id().value(),
        cart.customerId().value(),
        items,
        total.amount(),
        total.currency().getCurrencyCode());
  }
}
