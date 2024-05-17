insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Manya', 'Whitcomb', 'manya.whitcomb', '$2a$10$cZaltgerD6no7a1Zp5Re9uH/irQp3fKzf1YdCrurdv6/0MR/h1ioe', true);
-- vbxowmkpue
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Shea', 'McFater', 'shea.mcfater', '$2a$10$AQ6OLoJw6h0SDgig2QI.GeQpfIayhGoCAHvsaKMv.SVyKCrp1TaeS', true);
-- pmilyjaewb
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Miquela', 'Trembley', 'miquela.trembley', '$2a$10$umWZR.yFjm1nTSt..YhyLO1V0l9hCMwjjBRYmyulhpI178exuLGtG', true);
-- lvuhcyjdmw
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Roddy', 'Patman', 'roddy.patman', '$2a$10$4h2vde8Ib15hjUvge9wEluBh33zFOFd6RtG8pd6idK2yxvP1EOQN2', true);
-- aulecriyox
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Betteann', 'Staten', 'betteann.staten', '$2a$10$5/LcLxM1FEZX6wHs/Jq3buofe4.kP4XXOUjiw8NVZfDlbvAnSx5Wi', true);
-- npwrbgzsom
insert into USERS (FIRST_NAME, LAST_NAME, USERNAME, PASSWORD, IS_ACTIVE) values ('Ricardo', 'Patino', 'ricardo.patino', '$2a$10$JlBYH.oDN0JP2x5jD72fauR9b2G85kY6WyiErlUcCCla21nHK6LLq', true);
-- password

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