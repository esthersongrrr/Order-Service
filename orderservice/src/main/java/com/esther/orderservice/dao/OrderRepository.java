package com.esther.orderservice.dao;

import com.esther.orderservice.entity.Order;
import org.springframework.data.cassandra.repository.CassandraRepository;
import java.util.UUID;

public interface OrderRepository extends CassandraRepository<Order, UUID> {
}