package com.payment.stripecheckoutdemo.order;

import java.util.UUID;

public record OrderResponse(
        UUID id,
        String productName,
        String status,
        long amount,
        String currency
) {
    static OrderResponse of(Order order){
        return new OrderResponse(order.getId(), order.getProductName(),
                order.getStatus().name(), order.getAmount(), order.getCurrency());
    }
}
