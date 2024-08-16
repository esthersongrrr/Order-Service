package com.esther.orderservice.payload;

import com.esther.orderservice.entity.Order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class OrderDto {
    private UUID id;
    private String status; // e.g., Created, Paid, Completed, Cancelled
    private String details; // JSON string or a complex object serialized to string
    private Date createdAt;

    private BigDecimal amount;
    private String address;

    private Map<String, Integer> items;

    public OrderDto() {}

    public OrderDto(UUID id, String status, String details, Date createdAt, BigDecimal amount, String address, Map<String, Integer> items) {
        this.id = id;
        this.status = status;
        this.details = details;
        this.createdAt = createdAt;
        this.items = items;
        this.address = address;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
