# stripe-checkout-demo

Spring Boot demo of Stripe Checkout, webhook handling and payment idempotency.

**Stack:** Java 17, Spring Boot 4.1.1, PostgreSQL 18, Flyway, stripe-java 33.4.1

> Status: project setup only. Features are added step by step (see Roadmap).

## Prerequisites
- Java 17
- Maven
- PostgreSQL (local or Docker)
- A Stripe account in **test mode**
- [Stripe CLI](https://docs.stripe.com/stripe-cli)

## Setup

### 1. Database
```sql
CREATE USER stripe_demo WITH PASSWORD 'change_me';
CREATE DATABASE stripe_demo OWNER stripe_demo;
```

### 2. Local configuration
Copy `src/main/resources/application-dev.yaml.example` to `application-dev.yaml` and fill in:

| Property | Where to get it |
|---|---|
| `stripe.secret-key` | Stripe Dashboard, Developers, API keys (`sk_test_...`) |
| `stripe.webhook-secret` | Printed by `stripe listen` (`whsec_...`) |
| `spring.datasource.password` | Your local database password |

`application-dev.yaml` is git-ignored. Never commit real keys.

### 3. Stripe CLI (webhooks)
```bash
stripe login
stripe listen --events checkout.session.completed,checkout.session.async_payment_succeeded,checkout.session.async_payment_failed,checkout.session.expired --forward-to localhost:8282/api/webhooks/stripe
```
Keep this terminal open while testing webhooks.

### 4. Run
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
In IntelliJ: Run, Edit Configurations, Active profiles = `dev`.

The app listens on **http://localhost:8282**.

## Roadmap
- [x] Step 1: project setup and configuration
- [ ] Step 2: Flyway migration, `Order` entity, idempotent order creation
- [ ] Step 3: Checkout Session with Stripe idempotency key
- [ ] Step 4: webhook endpoint and signature verification
- [ ] Step 5: webhook idempotency and order state transitions
- [ ] Step 6: end-to-end test and hardening