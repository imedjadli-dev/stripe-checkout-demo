package com.payment.stripecheckoutdemo.order;

import com.payment.stripecheckoutdemo.config.StripeProperties;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;
    private final StripeProperties props;

    public CheckoutService(OrderRepository orderRepository, StripeProperties props) {
        this.orderRepository = orderRepository;
        this.props = props;
    }

    /** Deliberately NOT @Transactional: never hold a DB transaction open during a network call. */
    public String createCheckoutUrl(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order is already paid");
        }
        if (order.getStatus() == OrderStatus.PENDING && order.getCheckoutUrl() != null) {
            return order.getCheckoutUrl();
        }

        if (order.getStatus() == OrderStatus.EXPIRED || order.getStatus() == OrderStatus.FAILED) {
            try {
                order.startNewAttempt();
                order = orderRepository.saveAndFlush(order);
            } catch (OptimisticLockingFailureException concurrent) {
                // A parallel request already started the new attempt; continue from its state.
                order = orderRepository.findById(orderId).orElseThrow();
            }
        }

        Session session = createSession(order);

        try {
            order.attachSession(session.getId(), session.getUrl());
            orderRepository.saveAndFlush(order);
        } catch (OptimisticLockingFailureException concurrent) {
            // A parallel request saved first. Stripe's idempotency key guarantees it stored the SAME session.
        }
        return session.getUrl();
    }

    private Session createSession(Order order) {
        String orderId = order.getId().toString();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(orderId)
                .setSuccessUrl(props.successUrl())
                .setCancelUrl(props.cancelUrl())
                .putMetadata("order_id", orderId)
                .setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
                        .putMetadata("order_id", orderId)
                        .build())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(order.getCurrency())
                                .setUnitAmount(order.getAmount())
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(order.getProductName())
                                        .build())
                                .build())
                        .build())
                .build();

        RequestOptions options = RequestOptions.builder()
                .setApiKey(props.secretKey())
                .setIdempotencyKey("checkout-" + order.getId() + "-" + order.getCheckoutAttempt())
                .build();

        try {
            return Session.create(params, options);
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Stripe error: " + e.getMessage(), e);
        }
    }
}