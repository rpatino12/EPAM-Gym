package com.rpatino12.epam.gym.util;

import com.rpatino12.epam.gym.service.TraineeService;
import com.rpatino12.epam.gym.service.TrainerService;
import com.rpatino12.epam.gym.service.TrainingService;
import com.rpatino12.epam.gym.service.UserService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AppStartupEvent implements ApplicationListener<ApplicationReadyEvent> {
    private final UserService userService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public AppStartupEvent(UserService userService, TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.userService = userService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("The gym just started!");
    }
}
