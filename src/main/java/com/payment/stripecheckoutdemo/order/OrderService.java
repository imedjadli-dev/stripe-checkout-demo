package com.payment.stripecheckoutdemo.order;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Order create(String key , String productName,long amount,
            String currency){
        var existing = orderRepository.findByIdempotencyKey(key);
        if(existing.isPresent()){
            return requireSamePayload(existing.get(), productName, amount,
                    currency);
        }

        try {
            return orderRepository.saveAndFlush(new Order(key, productName, amount,
                    currency));
        }
        catch (DataIntegrityViolationException race){
            Order winner =
                    orderRepository.findByIdempotencyKey(key).orElseThrow(()-> race);
            return requireSamePayload(winner,productName,amount,currency);
        }
    }

    public Order getOrder(UUID id){
        return  orderRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

    }

    private Order requireSamePayload(Order order , String name ,
            long amount,
            String currency) {
        boolean same = order.getProductName().equals(name)                && order.getAmount() == amount
                && order.getCurrency().equalsIgnoreCase(currency);

        if(!same){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Idempotency-Key was already used with a different " +
                            "payload");
        }
        return order;
    }

}
