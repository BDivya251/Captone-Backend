package com.vehiclemanagement.apigateway.service;

import com.vehiclemanagement.apigateway.config.RabbitMQConfig;
import com.vehiclemanagement.apigateway.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmail(EmailEvent emailEvent) {
        log.info("Sending email to queue: {}", emailEvent.getTo());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailEvent
        );
        log.info("Email event sent to RabbitMQ queue for recipient: {}", emailEvent.getTo());
    }
}
