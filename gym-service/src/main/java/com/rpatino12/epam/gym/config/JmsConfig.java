package com.rpatino12.epam.gym.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rpatino12.epam.gym.util.MyMessageConverter;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.text.SimpleDateFormat;

@Configuration
public class JmsConfig {

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

    @Bean
    public ActiveMQConnectionFactory connectionFactory(){
        return new ActiveMQConnectionFactory(
                "admin",
                "admin",
                "tcp://localhost:61616");
    }

    // We deleted the JmsTemplate bean, because it was overriding the default one provided by Spring
    // Now we are going to work with the default JmsTemplate and it automatically uses the jacksonJmsConverter

    @Bean
    public DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory(){
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setMessageConverter(jacksonJmsMessageConverter());
        return factory;
    }
}
