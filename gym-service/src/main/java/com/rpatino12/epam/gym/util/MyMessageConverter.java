package com.rpatino12.epam.gym.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpatino12.epam.gym.dto.TrainerMonthlySummary;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
public class MyMessageConverter extends MappingJackson2MessageConverter {

    // This message converter is needed to set the MessageConverter bean in the JmsConfig class

    // We need to override the fromMessage() method to convert the received JSON in the message back to an
    // ArrayList of objects TrainerMonthlySummary (ArrayList<TrainerMonthlySummary>), because we were getting
    // from the @JmsListener method an ArrayList of LinkedHashMap Objects
    @Override
    public @NonNull Object fromMessage(@NonNull Message message) throws JMSException, MessageConversionException {
        TextMessage textMessage = (TextMessage) message;
        String json = textMessage.getText();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<TrainerMonthlySummary> list = null;
        try {
            // The TypeReference is used to specify the type of the ArrayList
            list = mapper.readValue(json, new TypeReference<ArrayList<TrainerMonthlySummary>>(){});
        } catch (IOException e) {
            log.error("Error while converting message: ", e);
        }

        return Objects.requireNonNullElseGet(list, ArrayList::new);
    }

}
