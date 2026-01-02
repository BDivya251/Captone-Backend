package com.vehiclemanagement.apigateway.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org. springframework.amqp.support. converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation. Bean;
import org.springframework. context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * Defines queues, exchanges, and bindings for notifications
 */
@Configuration
public class RabbitMQConfig {
    
    // Queue names
    public static final String USER_REGISTRATION_QUEUE = "user.registration.queue";
    public static final String BOOKING_CONFIRMATION_QUEUE = "booking. confirmation.queue";
    public static final String BILL_NOTIFICATION_QUEUE = "bill.notification. queue";
    
    // Exchange name
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    
    // Routing keys
    public static final String USER_REGISTRATION_KEY = "user.registration";
    public static final String BOOKING_CONFIRMATION_KEY = "booking.confirmation";
    public static final String BILL_NOTIFICATION_KEY = "bill.notification";
    
    /**
     * User Registration Queue
     */
    @Bean
    public Queue userRegistrationQueue() {
        return QueueBuilder.durable(USER_REGISTRATION_QUEUE)
                .withArgument("x-message-ttl", 86400000) // 24 hours TTL
                .build();
    }
    
    /**
     * Booking Confirmation Queue
     */
    @Bean
    public Queue bookingConfirmationQueue() {
        return QueueBuilder. durable(BOOKING_CONFIRMATION_QUEUE).build();
    }
    
    /**
     * Bill Notification Queue
     */
    @Bean
    public Queue billNotificationQueue() {
        return QueueBuilder.durable(BILL_NOTIFICATION_QUEUE).build();
    }
    
    /**
     * Topic Exchange (allows pattern-based routing)
     */
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }
    
    /**
     * Bind user registration queue to exchange
     */
    @Bean
    public Binding userRegistrationBinding(Queue userRegistrationQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(userRegistrationQueue)
                .to(notificationExchange)
                .with(USER_REGISTRATION_KEY);
    }
    
    /**
     * Bind booking confirmation queue
     */
    @Bean
    public Binding bookingConfirmationBinding(Queue bookingConfirmationQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(bookingConfirmationQueue)
                .to(notificationExchange)
                .with(BOOKING_CONFIRMATION_KEY);
    }
    
    /**
     * Bind bill notification queue
     */
    @Bean
    public Binding billNotificationBinding(Queue billNotificationQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(billNotificationQueue)
                .to(notificationExchange)
                .with(BILL_NOTIFICATION_KEY);
    }
    
    /**
     * JSON Message Converter (converts Java objects to JSON in RabbitMQ)
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * RabbitTemplate with JSON converter
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
