package com.esther.orderservice.payload;

import com.esther.orderservice.entity.Order;

import java.util.Date;
import java.util.UUID;

public class OrderDto {
    private UUID id;
    private String status; // e.g., Created, Paid, Completed, Cancelled
    private String details; // JSON string or a complex object serialized to string
    private Date createdAt;

    public OrderDto() {}

    public OrderDto(UUID id, String status, String details, Date createdAt) {
        this.id = id;
        this.status = status;
        this.details = details;
        this.createdAt = createdAt;
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
}
