CREATE TABLE USERS(
  USER_ID BIGSERIAL PRIMARY KEY,
  FIRST_NAME VARCHAR(64) NOT NULL,
  LAST_NAME VARCHAR(64) NOT NULL,
  USERNAME varchar_ignorecase(50) NOT NULL UNIQUE,
  PASSWORD varchar_ignorecase(500) NOT NULL,
  IS_ACTIVE BOOLEAN NOT NULL
);

CREATE TABLE TRAINEE(
  TRAINEE_ID BIGSERIAL PRIMARY KEY,
  BIRTHDATE DATE,
  ADDRESS VARCHAR(64),
  USER_ID BIGINT NOT NULL
);

CREATE TABLE TRAINER(
  TRAINER_ID BIGSERIAL PRIMARY KEY,
  SPECIALIZATION_ID BIGINT NOT NULL,
  USER_ID BIGINT NOT NULL
);

CREATE TABLE TRAINEE2TRAINER(
  TRAINEE2TRAINER_ID BIGSERIAL PRIMARY KEY,
  TRAINEE_ID BIGINT NOT NULL,
  TRAINER_ID BIGINT NOT NULL
);


CREATE TABLE TRAINING_TYPE(
  TRAINING_TYPE_ID BIGSERIAL PRIMARY KEY,
  TRAINING_TYPE_NAME VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE TRAINING(
  TRAINING_ID BIGSERIAL PRIMARY KEY,
  TRAINEE_ID BIGINT NOT NULL,
  TRAINER_ID BIGINT NOT NULL,
  TRAINING_NAME VARCHAR(64) NOT NULL,
  TRAINING_TYPE_ID BIGINT NOT NULL,
  TRAINING_DATE DATE NOT NULL,
  TRAINING_DURATION DOUBLE NOT NULL
);