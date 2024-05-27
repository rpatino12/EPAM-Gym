package com.rpatino12.epam.gym.service.jms;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorkloadMessageListener implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            String json = ((TextMessage) message).getText();
            log.info(json);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
