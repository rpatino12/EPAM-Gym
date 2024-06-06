Feature: Get Workloads
  Get all workloads of the trainers with registered training sessions

  Scenario: Retrieve all trainers' workloads when there are multiple trainers in the database
    Given there are multiple trainers in the database
    When I retrieve all trainers' workloads
    Then I should receive a list of all trainers and their corresponding workloads

  Scenario: Retrieve empty list of workloads when there are no trainers in the database
    Given there are no trainers' workloads registered in the database
    When I retrieve all trainers' workloads
    Then I should receive an empty list