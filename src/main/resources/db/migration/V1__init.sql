-- matchmaking-service initial schema

CREATE TABLE athlete_profiles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    training_level  VARCHAR(20) NOT NULL,
    preferences     VARCHAR(500),
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

CREATE TABLE athlete_profile_goals (
    athlete_profile_id BIGINT NOT NULL REFERENCES athlete_profiles(id) ON DELETE CASCADE,
    goal               VARCHAR(30) NOT NULL,
    PRIMARY KEY (athlete_profile_id, goal)
);

CREATE TABLE coach_profiles (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL UNIQUE,
    biography           VARCHAR(1000),
    years_of_experience INT NOT NULL,
    hourly_rate         NUMERIC(10, 2) NOT NULL,
    currency            VARCHAR(3),
    accepting_clients   BOOLEAN NOT NULL,
    average_rating      NUMERIC(3, 2) NOT NULL,
    total_reviews       INT NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL
);

CREATE TABLE coach_profile_specialties (
    coach_profile_id BIGINT NOT NULL REFERENCES coach_profiles(id) ON DELETE CASCADE,
    specialty        VARCHAR(30) NOT NULL,
    PRIMARY KEY (coach_profile_id, specialty)
);

CREATE TABLE availability_slots (
    id               BIGSERIAL PRIMARY KEY,
    day_of_week      VARCHAR(20),
    start_time       TIME,
    end_time         TIME,
    active           BOOLEAN NOT NULL,
    coach_profile_id BIGINT REFERENCES coach_profiles(id) ON DELETE CASCADE,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

CREATE INDEX idx_availability_slots_coach ON availability_slots(coach_profile_id);

CREATE TABLE connection_requests (
    id              BIGSERIAL PRIMARY KEY,
    athlete_user_id BIGINT NOT NULL,
    coach_user_id   BIGINT NOT NULL,
    message         VARCHAR(500),
    status          VARCHAR(20) NOT NULL,
    responded_at    TIMESTAMP,
    response_note   VARCHAR(500),
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL
);

CREATE INDEX idx_connection_requests_athlete ON connection_requests(athlete_user_id);
CREATE INDEX idx_connection_requests_coach ON connection_requests(coach_user_id);

CREATE TABLE training_sessions (
    id               BIGSERIAL PRIMARY KEY,
    athlete_user_id  BIGINT NOT NULL,
    coach_user_id    BIGINT NOT NULL,
    scheduled_at     TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    location         VARCHAR(500),
    notes            VARCHAR(500),
    status           VARCHAR(20) NOT NULL,
    completed_at     TIMESTAMP,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL
);

CREATE INDEX idx_training_sessions_athlete ON training_sessions(athlete_user_id);
CREATE INDEX idx_training_sessions_coach ON training_sessions(coach_user_id);
