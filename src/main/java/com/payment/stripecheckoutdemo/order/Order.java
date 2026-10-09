package com.payment.stripecheckoutdemo.order;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID id = UUID.randomUUID();

    @Version
    private Long version;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(name = "product_name", nullable = false)
    private String productName;

    /** Minor units (cents). Never use floating point for money. */
    @Column(nullable = false)
    private long amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "checkout_session_id")
    private String checkoutSessionId;

    @Column(name = "checkout_url", length = 2048)
    private String checkoutUrl;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "checkout_attempt", nullable = false)
    private int checkoutAttempt = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Order() {
    }

    public Order(String idempotencyKey, String productName, long amount, String currency) {
        this.idempotencyKey = idempotencyKey;
        this.productName = productName;
        this.amount = amount;
        this.currency = currency.toLowerCase(Locale.ROOT);
    }

    public UUID getId() { return id; }
    public Long getVersion() { return version; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getProductName() { return productName; }
    public long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public OrderStatus getStatus() { return status; }
    public String getCheckoutSessionId() { return checkoutSessionId; }
    public String getCheckoutUrl() { return checkoutUrl; }
    public String getPaymentIntentId() { return paymentIntentId; }
    public int getCheckoutAttempt() { return checkoutAttempt; }
    public Instant getCreatedAt() { return createdAt; }


    public void attachSession(String sessionId, String url){
        this.checkoutSessionId = sessionId;
        this.checkoutUrl = url;
    }

    public void startNewAttempt(){
        this.checkoutAttempt++;
        this.checkoutSessionId=null;
        this.checkoutUrl=null;
        this.status= OrderStatus.PENDING;
    }
}