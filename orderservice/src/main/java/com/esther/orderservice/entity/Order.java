package com.esther.orderservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Table("orders")
public class Order {
    @PrimaryKey
    private UUID id;

    @Column("status")
    private String status; // e.g., Created, Paid, Completed, Cancelled

    @Column("details")
    private String details; // JSON string or a complex object serialized to string

    @Column("createdat")
    private Date createdAt;

    @Column("amount")
    private BigDecimal amount;

    @Column("shippingaddress")
    private String address;

    @Column("items")
    private Map<String, Integer> items;

    // Constructors, Getters and Setters
    public Order() {
    }
    public Order(UUID id, String status, String details, Date createdAt, BigDecimal amount, String address, Map<String, Integer> items) {
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
