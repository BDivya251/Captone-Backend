package com.vehiclemanagement.userservice.config;


import org. springframework.amqp.rabbit.connection.ConnectionFactory;
import org. springframework.amqp.rabbit. core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org. springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration for User Service
 */
@Configuration
public class RabbitMQConfig {
    
    // Must match Notification Service
    public static final String NOTIFICATION_EXCHANGE = "notification. exchange";
    public static final String USER_REGISTRATION_KEY = "user.registration";
    
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}