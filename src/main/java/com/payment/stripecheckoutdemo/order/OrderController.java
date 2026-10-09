package com.payment.stripecheckoutdemo.order;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final CheckoutService checkoutService;
    public OrderController(OrderService orderService , CheckoutService checkoutService) {
        this.orderService = orderService;
        this.checkoutService= checkoutService;
    }

    @PostMapping
    public OrderResponse create(@RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        return OrderResponse.of(orderService.create(
                idempotencyKey, request.productName(), request.amount(), request.currency()));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        return OrderResponse.of(orderService.getOrder(id));
    }

    @PostMapping("/{id}/checkout")
    public Map<String, String> checkout(@PathVariable UUID id) {
        return Map.of("url", checkoutService.createCheckoutUrl(id));
    }
}