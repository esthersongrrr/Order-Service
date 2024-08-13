package com.esther.orderservice.service;

import com.esther.orderservice.dao.OrderRepository;
import com.esther.orderservice.entity.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setId(UUID.randomUUID());
        order.setCreatedAt(new Date());
        return orderRepository.save(order);
    }

    public Order updateOrder(UUID id, Order order) {
        return orderRepository.save(order); // Ensure you handle state changes correctly
    }

    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus("Cancelled");
        orderRepository.save(order);
    }

    public Order getOrderById(UUID id) {
        return orderRepository.findById(id).orElseThrow();
    }
}
