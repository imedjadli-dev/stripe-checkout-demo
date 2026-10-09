CREATE TABLE orders (
                        id                  UUID         PRIMARY KEY,
                        version             BIGINT       NOT NULL,
                        idempotency_key     VARCHAR(255) NOT NULL,
                        product_name        VARCHAR(255) NOT NULL,
                        amount              BIGINT       NOT NULL CHECK (amount > 0),
                        currency            VARCHAR(3)   NOT NULL,
                        status              VARCHAR(20)  NOT NULL,
                        checkout_session_id VARCHAR(255),
                        checkout_url        VARCHAR(2048),
                        payment_intent_id   VARCHAR(255),
                        checkout_attempt    INTEGER      NOT NULL DEFAULT 0,
                        created_at          TIMESTAMPTZ  NOT NULL,
                        CONSTRAINT uk_orders_idempotency_key UNIQUE (idempotency_key)
);

CREATE INDEX idx_orders_checkout_session_id ON orders (checkout_session_id);