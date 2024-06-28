package com.rpatino12.epam.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rpatino12.epam.gym.util.MyMessageConverter;
import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.text.SimpleDateFormat;

@Configuration
@Slf4j
@Profile("!without-db-and-queue")
public class JmsConfig {

    // By adding the properties to the application.yaml for ActiveMQ Spring Boot will automatically configure
    // the ConnectionFactory and the JmsTemplate
    @Autowired
    private ConnectionFactory connectionFactory;

    // Make sure to import the org.springframework.jms.support.converter.MessageConverter in order to the app work properly
    // MappingJackson2MessageConverter is designed to work with JSON Strings
    @Bean
    public MessageConverter jacksonJmsMessageConverter(){
        // Here we're setting our custom message converter as the default message converter for our JMS template
        MappingJackson2MessageConverter converter = new MyMessageConverter();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        converter.setObjectMapper(mapper);
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
