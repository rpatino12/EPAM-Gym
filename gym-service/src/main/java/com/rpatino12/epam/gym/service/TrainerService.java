package com.rpatino12.epam.gym.service;

import com.rpatino12.epam.gym.dto.TrainerMonthlySummary;
import com.rpatino12.epam.gym.dto.UserLogin;
import com.rpatino12.epam.gym.exception.ResourceNotFoundException;
import com.rpatino12.epam.gym.exception.TrainerNullException;
import com.rpatino12.epam.gym.model.User;
import com.rpatino12.epam.gym.repo.TrainerRepository;
import com.rpatino12.epam.gym.model.Trainer;
import jakarta.annotation.PostConstruct;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;

@Service
@Slf4j
public class TrainerService {

    private final PasswordEncoder passwordEncoder;
    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private static final String WORKLOAD_REQUEST_QUEUE = "workload.request.queue";
    private static final String WORKLOAD_RESPONSE_QUEUE = "workload.response.queue";
    private List<TrainerMonthlySummary> monthlySummaryList = new ArrayList<>();

    private final JmsTemplate jmsTemplate;

    public TrainerService(PasswordEncoder passwordEncoder, TrainerRepository trainerRepository, UserService userService, JmsTemplate jmsTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.trainerRepository = trainerRepository;
        this.userService = userService;
        this.jmsTemplate = jmsTemplate;
    }

    // Trainer Service class should support possibility to create/update/select Trainer profile.
    @Transactional
    public UserLogin save(Trainer newTrainer){
        if (null == newTrainer){
            log.error("Cannot save a null or empty entity");
            throw new TrainerNullException("Trainer cannot be null");
        }
        newTrainer.setUser(userService.registerUser(newTrainer.getUser()));
        Trainer trainer = trainerRepository.save(newTrainer);
        UserLogin userLogin = new UserLogin(trainer.getUser().getUsername(), trainer.getUser().getPassword());
        trainer.getUser().setPassword(passwordEncoder.encode(trainer.getUser().getPassword()));
        log.info("Saved trainer: " + trainer);

        return userLogin;
    }

    @Transactional
    public Trainer update(Trainer updatedTrainer, String username){
        if (null == updatedTrainer){
            log.error("Cannot update trainer");
            throw new TrainerNullException("Trainer cannot be null");
        }
        User updatedUser = userService.updateUser(updatedTrainer.getUser(), username);
        updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        if (trainerRepository.findTrainerByUserUsername(username).isEmpty()){
            log.error("The entity to be updated does not exist");
            throw new ResourceNotFoundException("Trainer", "username", username);
        }
        Trainer trainer = trainerRepository.findTrainerByUserUsername(username).get();
        trainer.setSpecialization(updatedTrainer.getSpecialization());
        trainer.setUser(updatedUser);

        log.info("""
                        Updating trainer {}:\s
                        First Name: {}\s
                        Last Name: {}\s
                        Specialization Id: {}\s
                        Is Active: {}""",
                username,
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                trainer.getSpecialization().getTrainingTypeId(),
                updatedUser.getIsActive());

        return trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    public List<Trainer> getAll(){
        log.info("Getting all trainers");
        List<Trainer> trainers = trainerRepository.findAll();
        if (trainers.isEmpty()){
            log.error("There are no trainees registered");
            throw new ResourceNotFoundException("Trainer");
        }
        return trainers;
    }

    @Transactional(readOnly = true)
    public Optional<Trainer> getByUsername(String username){
        log.info("Searching trainer: " + username);
        return trainerRepository.findTrainerByUserUsername(username);
    }

    @Transactional
    public String updatePassword(String username, String oldPassword, String newPassword){
        log.info("Updating trainer password");
        Trainer trainer = this.getByUsername(username).orElse(new Trainer());
        String result = "";
        if (null==trainer.getUser() || !passwordEncoder.matches(oldPassword, trainer.getUser().getPassword())){
            result = "Wrong username or password";
            log.error(result);
        } else {
            if (oldPassword.equals(newPassword)) {
                result = "New password cannot be the same as old password";
                log.error(result);
            } else if (newPassword.isEmpty()) {
                result = "New password cannot be empty, please enter new password";
                log.error(result);
            } else {
                trainer.setUser(userService.updateUserPassword(newPassword, trainer.getUser().getId()));
                result = "Password updated";
                log.info(result);
            }
        }
        return result;
    }

    @Transactional
    public String updateActiveStatus(String username, String password){
        Trainer trainer = this.getByUsername(username).orElse(new Trainer());
        String result = "";
        if (null==trainer.getUser() || !passwordEncoder.matches(password, trainer.getUser().getPassword())){
            result = "Wrong username or password";
            log.error(result);
        } else {
            if (trainer.getUser().getIsActive()){
                trainer.setUser(userService.updateStatus(false, trainer.getUser().getId()));
                result = "User deactivated";
                log.info(result);
            } else {
                trainer.setUser(userService.updateStatus(true, trainer.getUser().getId()));
                result = "User activated";
                log.info(result);
            }
        }
        return result;
    }

    @Transactional
    public List<TrainerMonthlySummary> getAllWorkloads(){
        log.info("Getting the registered monthly workloads of all trainers");
        requestWorkloads();
        if (monthlySummaryList.isEmpty()){
            log.error("There are no training sessions registered yet for any trainer");
            throw new ResourceNotFoundException("Trainers workload");
        }
        return monthlySummaryList;
    }

    @Transactional
    public void requestWorkloads(){
        log.info("Requesting the monthly workloads to trainer-service");
        jmsTemplate.convertAndSend(WORKLOAD_REQUEST_QUEUE, "Request for all workloads");
    }

    @JmsListener(destination = WORKLOAD_RESPONSE_QUEUE)
    public void receiveWorkloads(List<TrainerMonthlySummary> workloads){
        if (workloads == null) {
            throw new IllegalArgumentException("Workloads list cannot be null");
        }
        if (workloads.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Workloads list cannot contain null elements");
        }
        try {
            monthlySummaryList = workloads;
        } catch (Exception e){
            log.error("Error occurred while receiving workloads", e);
        }
    }

    public Double getMonthlySummary(String username, YearMonth yearMonth){
        log.info("Getting {}'s workload summary of trainer {}", yearMonth.getMonth().toString().toLowerCase(), username);
        Optional<TrainerMonthlySummary> monthlySummaryOptional = monthlySummaryList.stream()
                .filter(trainerMonthlySummary -> trainerMonthlySummary.getUsername().equals(username))
                .findFirst();

        if (monthlySummaryOptional.isPresent()){
            TrainerMonthlySummary monthlySummary = monthlySummaryOptional.get();
            return monthlySummary.getMonthlySummary().getOrDefault(yearMonth.toString(), 0.0);
        } else {
            log.error("There are no training sessions registered for this trainer");
            return 0.0;
        }
    }

    @PostConstruct
    public void init(){
        log.info("Starting TrainerService");
    }
}
