package com.rpatino12.epam.gym;

import com.rpatino12.epam.gym.model.TrainingType;
import com.rpatino12.epam.gym.model.TrainingTypes;
import com.rpatino12.epam.gym.repo.TrainingTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class GymApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymApplication.class, args);
	}

	// Here we are going to save the Training Types for the 'prod' Spring profile, only if the table is empty
	@Bean
	@Profile("prod")
	CommandLineRunner commandLineRunner(TrainingTypeRepository trainingType){
		return args -> {
			if (trainingType.count() == 0){
				trainingType.save(new TrainingType(1L, TrainingTypes.Fitness));
				trainingType.save(new TrainingType(2L, TrainingTypes.Yoga));
				trainingType.save(new TrainingType(3L, TrainingTypes.Zumba));
				trainingType.save(new TrainingType(4L, TrainingTypes.Stretching));
				trainingType.save(new TrainingType(5L, TrainingTypes.Resistance));
			}
		};
	}

}
