package com.esther.orderservice.service;

import com.esther.orderservice.dao.OrderRepository;
import com.esther.orderservice.entity.Order;
import com.esther.orderservice.payload.Order2ItemDto;
import com.esther.orderservice.payload.OrderDto;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    private OrderRepository orderRepository;
    private Gson gson;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // could lead to uneven load distribution across partitions
    public OrderService(OrderRepository orderRepository, Gson gson) {
        this.orderRepository = orderRepository;
        this.gson = gson; // Initialize Gson
    }

    public OrderDto createOrder(OrderDto orderdto) {
        orderdto.setId(UUID.randomUUID());
        orderdto.setCreatedAt(new Date());
        orderdto.setStatus("Created");
        orderRepository.save(orderDto2Order(orderdto));

        Order2ItemDto newO2I = new Order2ItemDto(orderdto.getId(), orderdto.getItems(), orderdto.getAmount());
        String orderJson = gson.toJson(newO2I);
        System.out.println("Sending JSON to Kafka: " + orderJson);
        kafkaTemplate.send("order-created", orderdto.getId().toString(), orderJson);
        return orderdto;
    }


//    public OrderDto updateOrder(UUID id, OrderDto orderdto) {
//        orderRepository.save(orderDto2Order(orderdto));
//        return orderdto; // Ensure you handle state changes correctly
//    }

    public OrderDto updateOrder(UUID id, Map<String, Object> updates) {
        Order order = orderRepository.findById(id).orElseThrow();
        updates.forEach((key, value) -> {
            switch (key) {
                case "amount":
                    if ( value == order.getAmount()) {
                        throw new RuntimeException("Amount not change.");
                    }
                    order.setAmount((BigDecimal) value);
                    break;
                case "status":
                    if (value.equals(order.getStatus())) {
                        throw new RuntimeException("Status not change.");
                    }
                    order.setStatus((String) value);
                    break;
                case "details":
                    order.setDetails((String) value);
                    break;
                case "address":
                    if (value.equals(order.getAddress())) {
                        throw new RuntimeException("Addresses not change.");
                    }
                    order.setAddress((String) value);
                    break;
            }
        });
        orderRepository.save(order);

//        // Serialize OrderDto to JSON
//        String orderJson = gson.toJson(orderdto);
//        kafkaTemplate.send("order-updated", orderdto.getId().toString(), orderJson);

        return order2OrderDto(order);
    }

    public void cancelOrder(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus("Cancelled");
        orderRepository.save(order);

        // Serialize OrderDto to JSON
        String orderJson = gson.toJson(order2OrderDto(order));
        kafkaTemplate.send("order-cancelled", order2OrderDto(order).getId().toString(), orderJson);
    }

    public OrderDto getOrderById(UUID id) {
        return order2OrderDto(orderRepository.findById(id).orElseThrow());
    }

    // Listener for order creations
    @KafkaListener(topics = "order-created")
    public void handleOrderCreated(String json) {
        OrderDto orderDto = gson.fromJson(json, OrderDto.class);
        UUID orderId = orderDto.getId();
        System.out.println("Received order creation for order ID: " + orderId);
    }

    // Listener for order updates
    @KafkaListener(topics = "order-updated")
    public void handleOrderUpdated(String json) {
        OrderDto orderDto = gson.fromJson(json, OrderDto.class);
        System.out.println("Received order update for order ID: " + orderDto.getId() + "new amount:" + orderDto.getAmount() );
        // Implement additional logic as needed using the orderDto object
    }

    // Listener for order cancellations
    @KafkaListener(topics = "order-cancelled")
    public void handleOrderCancelled(String json) {
        OrderDto orderDto = gson.fromJson(json, OrderDto.class);
        System.out.println("Received order cancellation for order ID: " + orderDto.getId());

        Order2ItemDto newO2I = new Order2ItemDto(orderDto.getId(), orderDto.getItems(), orderDto.getAmount());
        String orderJson = gson.toJson(newO2I);
        System.out.println("Sending JSON to Kafka: " + orderJson);
        kafkaTemplate.send("order-failed", orderDto.getId().toString(), orderJson);
    }

    // Listener for order creations
    @KafkaListener(topics = "item-placed")
    public void handleOrderPlaced(String json) {
        Order2ItemDto orderDto = gson.fromJson(json, Order2ItemDto.class);
        UUID orderId = orderDto.getId();
        System.out.println("Received item placement for order ID: " + orderId);

        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setAmount(orderDto.getAmount());
        order.setItems(orderDto.getItems());
        order.setStatus("Placed");
        orderRepository.save(order);

        // Serialize OrderDto to JSON
        String orderJson = gson.toJson(order2OrderDto(order));
        System.out.println("Sending JSON to Kafka: " + orderJson);
        kafkaTemplate.send("order-placed", order.getId().toString(), orderJson);
    }


    @KafkaListener(topics = "payment-success")
    public void handlePaymentSuccess(String json) {
        OrderDto orderDto = gson.fromJson(json, OrderDto.class);
        UUID orderId = orderDto.getId();
        System.out.println("Received payment complete for order ID: " + orderId);
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus("Completed");
    }

    @KafkaListener(topics = "payment-refunded")
    public void handlePaymentRefunded(String json) {
        OrderDto orderDto = gson.fromJson(json, OrderDto.class);
        UUID orderId = orderDto.getId();
        System.out.println("Received payment refund for order ID: " + orderId);
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus("Cancelled");

        Order2ItemDto newO2I = new Order2ItemDto(order.getId(), order.getItems(), order.getAmount());
        String orderJson = gson.toJson(newO2I);
        System.out.println("Sending JSON to Kafka: " + orderJson);
        kafkaTemplate.send("order-failed", order.getId().toString(), orderJson);
    }

    private OrderDto order2OrderDto(Order order) {
        OrderDto od = new OrderDto();
        od.setId(order.getId());
        od.setCreatedAt(order.getCreatedAt());
        od.setStatus(order.getStatus());
        od.setDetails(order.getDetails());
        od.setAmount(order.getAmount());
        od.setItems(order.getItems());
        od.setAddress(order.getAddress());
        return od;
    }

    private Order orderDto2Order(OrderDto orderdto) {
        Order order = new Order();
        order.setId(orderdto.getId());
        order.setCreatedAt(orderdto.getCreatedAt());
        order.setStatus(orderdto.getStatus());
        order.setDetails(orderdto.getDetails());
        order.setAmount(orderdto.getAmount());
        order.setItems(orderdto.getItems());
        order.setAddress(orderdto.getAddress());
        return order;
    }
}
