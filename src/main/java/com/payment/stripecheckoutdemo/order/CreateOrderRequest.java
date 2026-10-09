package com.payment.stripecheckoutdemo.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank
        String productName,
        @Positive
        long amount,
        @NotBlank
        @Size(min = 3, max=3)
        String currency
) {
}
