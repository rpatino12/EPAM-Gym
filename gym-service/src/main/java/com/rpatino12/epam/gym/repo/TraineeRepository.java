package com.rpatino12.epam.gym.repo;

import com.rpatino12.epam.gym.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findTraineeByUserUsername(String username);
    void deleteByUserUsername(String username);
}
