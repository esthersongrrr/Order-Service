package com.esther.orderservice.controller;

import com.esther.orderservice.entity.Order;
import com.esther.orderservice.payload.OrderDto;
import com.esther.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto createOrder(@RequestBody OrderDto order) {
        return orderService.createOrder(order);
    }

    @PatchMapping("/{id}")
    public OrderDto updateOrder(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        return orderService.updateOrder(id, updates);
    }

    @PatchMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable UUID id) {
        orderService.cancelOrder(id);
    }

    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }
}
