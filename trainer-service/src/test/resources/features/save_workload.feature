Feature: Save Workload
  Save trainer workload when a new training is created in gym-service

  Scenario: Save a new workload for a trainer who does not exist in the database
    Given a Workload for a trainer with username "john.doe"
    And the trainer "john.doe" does not exist in the database
    When I save the workload
    Then a new trainer "john.doe" should be created in the system
    And the workload should be saved under the trainer "john.doe"

  Scenario: Save a new workload for an existing trainer
    Given a Workload for a trainer with username "jane.doe"
    And the trainer "jane.doe" exists in the database
    When I save the workload
    Then the workload should be updated under the trainer "jane.doe"
    And the monthly summary under the trainer "jane.doe" should be updated

  Scenario: Delete a workload for a trainer
    Given a request to delete the workload of the last training session of trainer "john.doe"
    And the trainer "john.doe" exists in the database
    When I save the workload
    Then the workload should be updated under the trainer "john.doe"
    And the monthly summary under the trainer "jane.doe" should be reduced by the last training session duration

  Scenario: Attempt to save a null workload IllegalArgumentException
    Given a null workload
    When I attempt to save the workload
    Then an IllegalArgumentException should be thrown