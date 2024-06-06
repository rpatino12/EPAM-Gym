Feature: Save Workload
  Save trainer workload when a new training is created in gym-service

  Scenario: Trainer workload is saved successfully
    Given a Workload for a trainer with username "john.doe"
    And the trainer "john.doe" does not exist in the database
    When I save the workload
    Then a new trainer "john.doe" should be created in the system
    And the workload should be saved under the trainer "john.doe"