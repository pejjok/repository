CREATE TYPE user_role AS ENUM (
    'ROLE_USER',
    'ROLE_SUPERVISOR',
    'ROLE_ADMIN'
);

CREATE TYPE request_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);

CREATE TYPE work_type AS ENUM (
    'QUALIFICATION_PROJECT',
    'COURSE_PROJECT',
    'COURSE_WORK'
);

CREATE TABLE specialties (
    id uuid PRIMARY KEY,
    code varchar NOT NULL UNIQUE,
    name varchar NOT NULL
);

CREATE TABLE groups (
    id uuid PRIMARY KEY,
    name varchar NOT NULL UNIQUE,
    specialty_id uuid NOT NULL
);

CREATE TABLE users (
    id uuid PRIMARY KEY,
    email varchar NOT NULL UNIQUE,
    password_hash varchar NOT NULL,
    full_name varchar NOT NULL,
    role user_role NOT NULL,
    created_at timestamptz DEFAULT NOW() NOT NULL
);

CREATE TABLE supervisor_specialties (
    supervisor_id uuid NOT NULL,
    specialty_id uuid NOT NULL,
    PRIMARY KEY (supervisor_id, specialty_id)
);

CREATE TABLE scientific_works (
    id uuid PRIMARY KEY,
    title varchar NOT NULL,
    annotation text NOT NULL,
    student_full_name varchar NOT NULL,
    group_id uuid NOT NULL,
    supervisor_id uuid NOT NULL,
    work_type work_type NOT NULL,
    publication_year int NOT NULL,
    is_archived boolean DEFAULT FALSE NOT NULL,
    created_at timestamptz DEFAULT NOW() NOT NULL,
    updated_at timestamptz
);

CREATE TABLE work_files (
    id uuid PRIMARY KEY,
    work_id uuid,
    file_name varchar NOT NULL,
    original_name varchar NOT NULL,
    file_size bigint,
    uploaded_at timestamptz DEFAULT NOW() NOT NULL
);

CREATE TABLE refresh_tokens (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    token uuid NOT NULL,
    expires_at timestamptz NOT NULL,
    created_at timestamptz DEFAULT NOW() NOT NULL
);

CREATE TABLE work_file_requests (
    id uuid PRIMARY KEY,
    work_file_id uuid NOT NULL,
    user_id uuid NOT NULL,
    status request_status DEFAULT 'PENDING' NOT NULL,
    expires_at timestamptz,
    created_at timestamptz DEFAULT NOW() NOT NULL
);

ALTER TABLE groups
    ADD CONSTRAINT fk_groups_specialty FOREIGN KEY (specialty_id)
        REFERENCES specialties (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;


ALTER TABLE supervisor_specialties
    ADD CONSTRAINT fk_supervisor_specialties_supervisor
        FOREIGN KEY (supervisor_id)
            REFERENCES users (id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;

ALTER TABLE supervisor_specialties
    ADD CONSTRAINT fk_supervisor_specialties_specialty
        FOREIGN KEY (specialty_id)
            REFERENCES specialties (id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;


ALTER TABLE scientific_works
    ADD CONSTRAINT fk_works_group
        FOREIGN KEY (group_id)
            REFERENCES groups (id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT;

ALTER TABLE scientific_works
    ADD CONSTRAINT fk_works_supervisor FOREIGN KEY (supervisor_id)
        REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE work_files
    ADD CONSTRAINT fk_files_work FOREIGN KEY (work_id)
        REFERENCES scientific_works (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE;

ALTER TABLE work_file_requests
    ADD CONSTRAINT fk_work_file_requests_file
        FOREIGN KEY (work_file_id)
            REFERENCES work_files (id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

ALTER TABLE work_file_requests
    ADD CONSTRAINT fk_work_file_requests_requester
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON UPDATE CASCADE
            ON DELETE CASCADE;