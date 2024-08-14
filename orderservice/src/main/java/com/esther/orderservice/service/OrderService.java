package com.esther.orderservice.service;

import com.esther.orderservice.dao.OrderRepository;
import com.esther.orderservice.entity.Order;
import com.esther.orderservice.payload.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    @Autowired
    private KafkaTemplate<String, OrderDto> kafkaTemplate;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderDto createOrder(OrderDto orderdto) {
        orderdto.setId(UUID.randomUUID());
        orderdto.setCreatedAt(new Date());
        orderdto.setStatus("Created");
        orderRepository.save(orderDto2Order(orderdto));
        kafkaTemplate.send("order-created", orderdto);
        return orderdto;
    }

    public OrderDto updateOrder(UUID id, OrderDto orderdto) {
        orderRepository.save(orderDto2Order(orderdto));
        return orderdto; // Ensure you handle state changes correctly
    }

    public OrderDto updateOrderStatus(UUID id, String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        OrderDto od = order2OrderDto(order);
        kafkaTemplate.send("order-updated", od);
        return od;
    }

    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus("Cancelled");
        orderRepository.save(order);
        kafkaTemplate.send("order-cancelled", order2OrderDto(order));
    }

    public OrderDto getOrderById(UUID id) {
        return order2OrderDto(orderRepository.findById(id).orElseThrow());
    }

    // Listener for order creations
    @KafkaListener(topics = "order-created")
    public void handleOrderCreated(OrderDto orderdto) {
        System.out.println("Received order creation for order ID: " + orderdto.getId());
        // Implement additional logic as needed
    }

    // Listener for order updates
    @KafkaListener(topics = "order-updated")
    public void handleOrderUpdated(OrderDto orderdto) {
        System.out.println("Received order update for order ID: " + orderdto.getId());
        // Implement additional logic as needed
    }

    // Listener for order cancellations
    @KafkaListener(topics = "order-cancelled")
    public void handleOrderCancelled(OrderDto orderdto) {
        System.out.println("Received order cancellation for order ID: " + orderdto.getId());
        // Implement additional logic as needed
    }

    private OrderDto order2OrderDto(Order order) {
        OrderDto od = new OrderDto();
        od.setId(order.getId());
        od.setCreatedAt(order.getCreatedAt());
        od.setStatus(order.getStatus());
        od.setDetails(order.getDetails());
        return od;
    }

    private Order orderDto2Order(OrderDto orderdto) {
        Order order = new Order();
        order.setId(orderdto.getId());
        order.setCreatedAt(orderdto.getCreatedAt());
        order.setStatus(orderdto.getStatus());
        order.setDetails(orderdto.getDetails());
        return order;
    }
}
