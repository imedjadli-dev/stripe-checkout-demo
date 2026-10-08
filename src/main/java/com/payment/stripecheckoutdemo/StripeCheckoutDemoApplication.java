package com.payment.stripecheckoutdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StripeCheckoutDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripeCheckoutDemoApplication.class, args);
    }

}
