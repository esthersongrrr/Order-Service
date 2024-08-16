package com.esther.orderservice.exception;

import org.springframework.kafka.listener.KafkaListenerErrorHandler;

public class KafkaLisenerError {
    public KafkaListenerErrorHandler myErrorHandler() {
        return (m, e) -> {
            System.out.println("Error handling message: " + m + " due to " + e);
            return null;
        };
    }
}
