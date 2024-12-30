package com.desafio.account.payable.infrastructure.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@SuppressWarnings("unused")
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "import-file";
    private static final String DLQ_NAME = QUEUE_NAME + ".dlq";
    private static final String EXCHANGE_NAME = "exchange";
    private static final String ROUTING_KEY = "account.payable";

    @Bean
    public Queue queue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", EXCHANGE_NAME);
        arguments.put("x-dead-letter-routing-key", DLQ_NAME);
        return new Queue(QUEUE_NAME, true, false, false, arguments);
    }

    @Bean
    public Queue dlq() {
        return new Queue(DLQ_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, TopicExchange exchange) {
        return BindingBuilder.bind(dlq).to(exchange).with(DLQ_NAME);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
