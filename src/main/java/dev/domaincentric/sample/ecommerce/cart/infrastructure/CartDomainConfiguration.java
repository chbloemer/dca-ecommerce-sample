package dev.domaincentric.sample.ecommerce.cart.infrastructure;

import dev.domaincentric.sample.ecommerce.cart.domain.service.CartTotalCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Cart context domain services.
 *
 * <p>Domain services are framework-independent but need to be instantiated as Spring beans so they
 * can be injected into application services.
 */
@Configuration
public class CartDomainConfiguration {

  @Bean
  public CartTotalCalculator cartTotalCalculator() {
    return new CartTotalCalculator();
  }
}
