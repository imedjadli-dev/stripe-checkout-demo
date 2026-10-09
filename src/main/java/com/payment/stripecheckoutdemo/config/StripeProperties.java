package com.payment.stripecheckoutdemo.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stripe")
public record StripeProperties (
        @NotBlank  String secretKey,
        @NotBlank String webhookSecret,
        @NotBlank String successUrl,
        @NotBlank String cancelUrl
){}
