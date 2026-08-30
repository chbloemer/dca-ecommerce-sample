package dev.domaincentric.sample.ecommerce.checkout.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import dev.domaincentric.sample.ecommerce.checkout.domain.model.BuyerInfo;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CartId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutLineItem;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutLineItemId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutSession;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CheckoutStep;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.CustomerId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.DeliveryAddress;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.PaymentProviderId;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.PaymentSelection;
import dev.domaincentric.sample.ecommerce.checkout.domain.model.ShippingOption;
import dev.domaincentric.sample.ecommerce.checkout.domain.readmodel.CheckoutCartSnapshot;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.Money;
import dev.domaincentric.sample.ecommerce.sharedkernel.domain.model.ProductId;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CheckoutStepValidatorTest {

  private static final Currency EUR = Currency.getInstance("EUR");
  private CheckoutStepValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CheckoutStepValidator();
  }

  @Nested
  @DisplayName("Invalid Session Tests")
  class InvalidSessionTests {

    @Test
    @DisplayName("null session redirects to cart")
    void nullSessionRedirectsToCart() {
      Optional<String> redirect = validator.validateStepAccess(null, CheckoutStep.BUYER_INFO);

      assertTrue(redirect.isPresent());
      assertEquals("/cart", redirect.get());
    }

    @Test
    @DisplayName("null session redirects to cart for any step")
    void nullSessionRedirectsToCartForAnyStep() {
      for (CheckoutStep step : CheckoutStep.values()) {
        Optional<String> redirect = validator.validateStepAccess(null, step);
        assertTrue(redirect.isPresent(), "Should redirect for step: " + step);
        assertEquals("/cart", redirect.get());
      }
    }
  }

  @Nested
  @DisplayName("Skip Ahead Prevention Tests")
  class SkipAheadTests {

    @Test
    @DisplayName("cannot skip to DELIVERY from BUYER_INFO")
    void cannotSkipToDeliveryFromBuyerInfo() {
      var session = createActiveSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.DELIVERY);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/buyer", redirect.get());
    }

    @Test
    @DisplayName("cannot skip to PAYMENT from BUYER_INFO")
    void cannotSkipToPaymentFromBuyerInfo() {
      var session = createActiveSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.PAYMENT);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/buyer", redirect.get());
    }

    @Test
    @DisplayName("cannot skip to REVIEW from BUYER_INFO")
    void cannotSkipToReviewFromBuyerInfo() {
      var session = createActiveSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.REVIEW);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/buyer", redirect.get());
    }

    @Test
    @DisplayName("cannot skip to CONFIRMATION from BUYER_INFO")
    void cannotSkipToConfirmationFromBuyerInfo() {
      var session = createActiveSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.CONFIRMATION);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/buyer", redirect.get());
    }

    @Test
    @DisplayName("cannot skip to PAYMENT from DELIVERY")
    void cannotSkipToPaymentFromDelivery() {
      var session = createSessionAtDelivery();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.PAYMENT);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/delivery", redirect.get());
    }

    @Test
    @DisplayName("cannot skip to REVIEW from PAYMENT")
    void cannotSkipToReviewFromPayment() {
      var session = createSessionAtPayment();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.REVIEW);

      assertTrue(redirect.isPresent());
      assertEquals("/checkout/payment", redirect.get());
    }
  }

  @Nested
  @DisplayName("Go Back Tests")
  class GoBackTests {

    @Test
    @DisplayName("can go back to BUYER_INFO from DELIVERY")
    void canGoBackToBuyerInfoFromDelivery() {
      var session = createSessionAtDelivery();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.BUYER_INFO);

      assertTrue(redirect.isEmpty(), "Should allow going back to BUYER_INFO");
    }

    @Test
    @DisplayName("can go back to BUYER_INFO from PAYMENT")
    void canGoBackToBuyerInfoFromPayment() {
      var session = createSessionAtPayment();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.BUYER_INFO);

      assertTrue(redirect.isEmpty(), "Should allow going back to BUYER_INFO");
    }

    @Test
    @DisplayName("can go back to DELIVERY from PAYMENT")
    void canGoBackToDeliveryFromPayment() {
      var session = createSessionAtPayment();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.DELIVERY);

      assertTrue(redirect.isEmpty(), "Should allow going back to DELIVERY");
    }

    @Test
    @DisplayName("can go back to BUYER_INFO from REVIEW")
    void canGoBackToBuyerInfoFromReview() {
      var session = createSessionAtReview();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.BUYER_INFO);

      assertTrue(redirect.isEmpty(), "Should allow going back to BUYER_INFO");
    }

    @Test
    @DisplayName("can go back to DELIVERY from REVIEW")
    void canGoBackToDeliveryFromReview() {
      var session = createSessionAtReview();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.DELIVERY);

      assertTrue(redirect.isEmpty(), "Should allow going back to DELIVERY");
    }

    @Test
    @DisplayName("can go back to PAYMENT from REVIEW")
    void canGoBackToPaymentFromReview() {
      var session = createSessionAtReview();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.PAYMENT);

      assertTrue(redirect.isEmpty(), "Should allow going back to PAYMENT");
    }
  }

  @Nested
  @DisplayName("Terminal State Tests")
  class TerminalStateTests {

    @Test
    @DisplayName("completed session allows CONFIRMATION access")
    void completedSessionAllowsConfirmationAccess() {
      var session = createCompletedSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.CONFIRMATION);

      assertTrue(redirect.isEmpty(), "Should allow CONFIRMATION access for completed session");
    }

    @Test
    @DisplayName("completed session redirects other steps to CONFIRMATION")
    void completedSessionRedirectsOtherStepsToConfirmation() {
      var session = createCompletedSession();

      for (CheckoutStep step :
          List.of(
              CheckoutStep.BUYER_INFO, CheckoutStep.DELIVERY,
              CheckoutStep.PAYMENT, CheckoutStep.REVIEW)) {
        Optional<String> redirect = validator.validateStepAccess(snapshot(session), step);
        assertTrue(redirect.isPresent(), "Should redirect for step: " + step);
        assertEquals("/checkout/confirmation", redirect.get());
      }
    }

    @Test
    @DisplayName("abandoned session redirects to cart")
    void abandonedSessionRedirectsToCart() {
      var session = createAbandonedSession();

      for (CheckoutStep step : CheckoutStep.values()) {
        Optional<String> redirect = validator.validateStepAccess(snapshot(session), step);
        assertTrue(redirect.isPresent(), "Should redirect for step: " + step);
        assertEquals("/cart", redirect.get());
      }
    }

    @Test
    @DisplayName("expired session redirects to cart")
    void expiredSessionRedirectsToCart() {
      var session = createExpiredSession();

      for (CheckoutStep step : CheckoutStep.values()) {
        Optional<String> redirect = validator.validateStepAccess(snapshot(session), step);
        assertTrue(redirect.isPresent(), "Should redirect for step: " + step);
        assertEquals("/cart", redirect.get());
      }
    }

    @Test
    @DisplayName("confirmed session allows CONFIRMATION access")
    void confirmedSessionAllowsConfirmationAccess() {
      var session = createConfirmedSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.CONFIRMATION);

      assertTrue(redirect.isEmpty(), "Should allow CONFIRMATION access for confirmed session");
    }

    @Test
    @DisplayName("confirmed session redirects other steps to CONFIRMATION")
    void confirmedSessionRedirectsOtherStepsToConfirmation() {
      var session = createConfirmedSession();

      for (CheckoutStep step :
          List.of(
              CheckoutStep.BUYER_INFO, CheckoutStep.DELIVERY,
              CheckoutStep.PAYMENT, CheckoutStep.REVIEW)) {
        Optional<String> redirect = validator.validateStepAccess(snapshot(session), step);
        assertTrue(redirect.isPresent(), "Should redirect for step: " + step);
        assertEquals("/checkout/confirmation", redirect.get());
      }
    }
  }

  @Nested
  @DisplayName("Valid Access Tests")
  class ValidAccessTests {

    @Test
    @DisplayName("allows access to current step")
    void allowsAccessToCurrentStep() {
      var session = createActiveSession();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.BUYER_INFO);

      assertTrue(redirect.isEmpty(), "Should allow access to current step");
    }

    @Test
    @DisplayName("allows access to DELIVERY when at DELIVERY step")
    void allowsAccessToDeliveryWhenAtDeliveryStep() {
      var session = createSessionAtDelivery();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.DELIVERY);

      assertTrue(redirect.isEmpty(), "Should allow access to DELIVERY step");
    }

    @Test
    @DisplayName("allows access to PAYMENT when at PAYMENT step")
    void allowsAccessToPaymentWhenAtPaymentStep() {
      var session = createSessionAtPayment();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.PAYMENT);

      assertTrue(redirect.isEmpty(), "Should allow access to PAYMENT step");
    }

    @Test
    @DisplayName("allows access to REVIEW when at REVIEW step")
    void allowsAccessToReviewWhenAtReviewStep() {
      var session = createSessionAtReview();

      Optional<String> redirect =
          validator.validateStepAccess(snapshot(session), CheckoutStep.REVIEW);

      assertTrue(redirect.isEmpty(), "Should allow access to REVIEW step");
    }
  }

  @Nested
  @DisplayName("getCurrentStepPath Tests")
  class GetCurrentStepPathTests {

    @Test
    @DisplayName("returns correct path for BUYER_INFO")
    void returnsCorrectPathForBuyerInfo() {
      var session = createActiveSession();

      String path = validator.getCurrentStepPath(snapshot(session));

      assertEquals("/checkout/buyer", path);
    }

    @Test
    @DisplayName("returns correct path for DELIVERY")
    void returnsCorrectPathForDelivery() {
      var session = createSessionAtDelivery();

      String path = validator.getCurrentStepPath(snapshot(session));

      assertEquals("/checkout/delivery", path);
    }

    @Test
    @DisplayName("returns correct path for PAYMENT")
    void returnsCorrectPathForPayment() {
      var session = createSessionAtPayment();

      String path = validator.getCurrentStepPath(snapshot(session));

      assertEquals("/checkout/payment", path);
    }

    @Test
    @DisplayName("returns correct path for REVIEW")
    void returnsCorrectPathForReview() {
      var session = createSessionAtReview();

      String path = validator.getCurrentStepPath(snapshot(session));

      assertEquals("/checkout/review", path);
    }

    @Test
    @DisplayName("returns correct path for CONFIRMATION")
    void returnsCorrectPathForConfirmation() {
      var session = createConfirmedSession();

      String path = validator.getCurrentStepPath(snapshot(session));

      assertEquals("/checkout/confirmation", path);
    }
  }

  // Helper methods to create test sessions in various states

  /** The validator decides on the read model the web adapters hold, not on the aggregate. */
  private static CheckoutCartSnapshot snapshot(final CheckoutSession session) {
    return CheckoutCartSnapshot.from(session);
  }

  private CheckoutSession createActiveSession() {
    return CheckoutSession.start(
        CartId.generate(),
        CustomerId.of(UUID.randomUUID().toString()),
        List.of(createLineItem()),
        Money.of(BigDecimal.valueOf(100), EUR),
        new TaxCalculator());
  }

  private CheckoutSession createSessionAtDelivery() {
    CheckoutSession session = createActiveSession();
    session.submitBuyerInfo(createBuyerInfo());
    return session;
  }

  private CheckoutSession createSessionAtPayment() {
    CheckoutSession session = createSessionAtDelivery();
    session.submitDelivery(createDeliveryAddress(), createShippingOption(), new TaxCalculator());
    return session;
  }

  private CheckoutSession createSessionAtReview() {
    CheckoutSession session = createSessionAtPayment();
    session.submitPayment(createPaymentSelection());
    return session;
  }

  private CheckoutSession createConfirmedSession() {
    CheckoutSession session = createSessionAtReview();
    session.confirm();
    return session;
  }

  private CheckoutSession createCompletedSession() {
    CheckoutSession session = createConfirmedSession();
    session.complete("ORD-123");
    return session;
  }

  private CheckoutSession createAbandonedSession() {
    CheckoutSession session = createActiveSession();
    session.abandon();
    return session;
  }

  private CheckoutSession createExpiredSession() {
    CheckoutSession session = createActiveSession();
    session.expire();
    return session;
  }

  private CheckoutLineItem createLineItem() {
    return CheckoutLineItem.of(
        CheckoutLineItemId.generate(),
        ProductId.generate(),
        "Test Product",
        Money.of(BigDecimal.valueOf(100), EUR),
        1,
        null);
  }

  private BuyerInfo createBuyerInfo() {
    return BuyerInfo.of("test@example.com", "John", "Doe", "+1234567890");
  }

  private DeliveryAddress createDeliveryAddress() {
    return DeliveryAddress.of("123 Main St", "Anytown", "12345", "Germany");
  }

  private ShippingOption createShippingOption() {
    return ShippingOption.of(
        "standard", "Standard Shipping", "3-5 business days", Money.of(BigDecimal.valueOf(5), EUR));
  }

  private PaymentSelection createPaymentSelection() {
    return PaymentSelection.of(PaymentProviderId.of("stripe"));
  }
}
