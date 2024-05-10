package com.rpatino12.epam.trainerservice.repo;

import com.rpatino12.epam.trainerservice.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
    boolean existsByUsername(String username);
}
