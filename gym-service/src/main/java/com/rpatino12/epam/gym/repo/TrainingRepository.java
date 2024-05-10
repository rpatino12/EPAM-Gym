package com.rpatino12.epam.gym.repo;

import com.rpatino12.epam.gym.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<List<Training>> findTrainingByTraineeUserUsername(String username);
    Optional<List<Training>> findTrainingByTrainerUserUsername(String username);
    Optional<Training> findByTrainingDateAndTrainerUserUsername(Date trainingDate, String username);
}
