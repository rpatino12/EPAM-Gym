Feature: Save trainings and update workloads
  Create trainings and send the trainers' workload to the secondary microservice trainer-service with JMS and ActiveMQ

  Scenario: Save a new training session for existing trainee and trainer
    Given a new training session for an existing trainee "alice.smith" and an existing trainer "bob.jones"
    When I save the training session
    Then the training session should be saved under the trainee "alice.smith" and the trainer "bob.jones"
    And a workload summary for the trainer "bob.jones" should be sent to the JMS queue

  Scenario: Attempt to save a new training session for a non-existing trainee
    Given a new training session for a non-existing trainee "alice.smith" and an existing trainer "bob.jones"
    When I save the training session
    Then the operation should return false

  Scenario: Attempt to save a new training session for a non-existing trainer
    Given a new training session for an existing trainee "alice.smith" and a non-existing trainer "bob.jones"
    When I save the training session
    Then the operation should return false

  Scenario: Attempt to save a null training session
    Given a null training session
    When I save the training session
    Then an error should be thrown

  Scenario: Delete a trainer's workload for a specific date
    Given there are multiple training sessions for trainer "bob.jones" on date "2022-01-01"
    When I delete the trainer's workload for date "2022-01-01"
    Then the last training session workload should be deleted for trainer "bob.jones" on date "2022-01-01"

  Scenario: Attempt to delete a trainer's workload for a date with no training sessions
    Given there are no training sessions for trainer "bob.jones" on date "2022-01-01"
    When I delete the trainer's workload for date "2022-01-01"
    Then a ResourceNotFoundException should be thrown