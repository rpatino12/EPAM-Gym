Feature: Get trainings
  Get all training sessions saved by trainer or trainee username

  Scenario: Retrieve all training sessions when there are multiple training sessions in the system
    Given there are multiple training sessions in the system
    When I retrieve all training sessions
    Then I should receive a list of all training sessions

  Scenario: Retrieve all training sessions when there are no training sessions in the system
    Given there are no training sessions in the system
    When I retrieve all training sessions
    Then a ResourceNotFoundException should be thrown

  Scenario: Retrieve all training sessions for a specific trainee when there are multiple training sessions for the trainee
    Given there are multiple training sessions for trainee "alice.smith"
    When I retrieve all training sessions for trainee "alice.smith"
    Then I should receive a list of all training sessions for trainee "alice.smith"

  Scenario: Retrieve all training sessions for a specific trainee when there are no training sessions for the trainee
    Given there are no training sessions for trainee "alice.smith"
    When I retrieve all training sessions for trainee "alice.smith"
    Then I should receive an empty list

  Scenario: Retrieve all training sessions for a specific trainer when there are multiple training sessions for the trainer
    Given there are multiple training sessions for trainer "bob.jones"
    When I retrieve all training sessions for trainer "bob.jones"
    Then I should receive a list of all training sessions for trainer "bob.jones"

  Scenario: Retrieve all training sessions for a specific trainer when there are no training sessions for the trainer
    Given there are no training sessions for trainer "bob.jones"
    When I retrieve all training sessions for trainer "bob.jones"
    Then I should receive an empty list