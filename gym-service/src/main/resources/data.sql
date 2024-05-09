insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Manya', 'Whitcomb', 'manya.whitcomb', 'vbxowmkpue', true);
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Shea', 'McFater', 'shea.mcfater', 'pmilyjaewb', true);
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Miquela', 'Trembley', 'miquela.trembley', 'lvuhcyjdmw', true);
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Roddy', 'Patman', 'roddy.patman', 'aulecriyox', true);
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Betteann', 'Staten', 'betteann.staten', 'npwrbgzsom', true);
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Ricardo', 'Patino', 'ricardo.patino', 'password', true);

insert into TRAINEE (BIRTHDATE, ADDRESS, USER_ID) values ('2001-05-15', '6612 Rockefeller Lane', 1);
insert into TRAINEE (BIRTHDATE, ADDRESS, USER_ID) values ('1993-08-24', '234 American Point', 2);
insert into TRAINEE (BIRTHDATE, ADDRESS, USER_ID) values ('1990-11-25', '0899 Stone Corner Circle', 3);

insert into TRAINING_TYPE (TRAINING_TYPE_NAME) values ('Fitness');
insert into TRAINING_TYPE (TRAINING_TYPE_NAME) values ('Yoga');
insert into TRAINING_TYPE (TRAINING_TYPE_NAME) values ('Zumba');
insert into TRAINING_TYPE (TRAINING_TYPE_NAME) values ('Stretching');
insert into TRAINING_TYPE (TRAINING_TYPE_NAME) values ('Resistance');

insert into TRAINER (SPECIALIZATION_ID, USER_ID) values (2, 4);
insert into TRAINER (SPECIALIZATION_ID, USER_ID) values (4, 5);
insert into TRAINER (SPECIALIZATION_ID, USER_ID) values (5, 6);

insert into TRAINEE2TRAINER (TRAINEE_ID, TRAINER_ID) values (1, 1);
insert into TRAINEE2TRAINER (TRAINEE_ID, TRAINER_ID) values (1, 2);
insert into TRAINEE2TRAINER (TRAINEE_ID, TRAINER_ID) values (2, 1);

insert into TRAINING (TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_TYPE_ID, TRAINING_DATE, TRAINING_DURATION) values (1, 1, 'Hard', 4, '2023-09-10', 94);
insert into TRAINING (TRAINEE_ID, TRAINER_ID, TRAINING_NAME, TRAINING_TYPE_ID, TRAINING_DATE, TRAINING_DURATION) values (1, 2, 'Easy', 1, '2023-02-08', 60);