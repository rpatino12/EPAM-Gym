package com.rpatino12.epam.trainerservice.service.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Receiver {

//    @JmsListener(destination = "training-queue")
//    public void receiveMessage(String training){
//        log.info("Training received: {}", training);
//    }
}
