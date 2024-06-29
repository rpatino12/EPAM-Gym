package com.rpatino12.epam.trainerservice.config;

import com.rpatino12.epam.trainerservice.model.Trainer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PreDestroy
    @Profile("!prod")
    public void dropCollections() {
        mongoTemplate.dropCollection(Trainer.class);
    }
}
