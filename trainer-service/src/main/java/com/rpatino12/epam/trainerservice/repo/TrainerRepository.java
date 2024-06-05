package com.rpatino12.epam.trainerservice.repo;

import com.rpatino12.epam.trainerservice.model.Trainer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends MongoRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    boolean existsByUsername(String username);
}
