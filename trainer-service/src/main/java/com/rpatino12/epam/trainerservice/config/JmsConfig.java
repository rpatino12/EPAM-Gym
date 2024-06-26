package com.rpatino12.epam.trainerservice.config;

import com.rpatino12.epam.trainerservice.dto.WorkloadDto;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class JmsConfig {

    // By adding the properties to the application.yaml for ActiveMQ Spring Boot will automatically configure
    // the ConnectionFactory and the JmsTemplate
    @Autowired
    private ConnectionFactory connectionFactory;

    // MappingJackson2MessageConverter is designed to work with JSON Strings
    @Bean
    public MessageConverter jacksonJmsMessageConverter(){
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("com.rpatino12.epam.gym.dto.WorkloadDto", WorkloadDto.class);

        converter.setTypeIdMappings(typeIdMappings);
        converter.setTargetType(MessageType.TEXT);
        // Important! This type property has to be the same in both microservices message converters
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    //We deleted the ActiveMQConnectionFactory bean because it will be derived from the application.yaml by Spring Boot

    // We deleted the JmsTemplate bean, because it was overriding the default one provided by Spring
    // Now we are going to work with the default JmsTemplate and it automatically uses the jacksonJmsConverter

    @Bean
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setErrorHandler(t -> {
            log.error("Handling error in listener for messages, error: {}", t.getMessage());
        });
        return factory;
    }
}
