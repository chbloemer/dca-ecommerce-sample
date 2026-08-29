package dev.domaincentric.sample.ecommerce.cart.adapter.incoming.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record AddToCartRequest(@NotBlank String productId, @Positive int quantity) {}
