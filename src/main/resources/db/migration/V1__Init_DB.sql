CREATE TYPE user_role AS ENUM (
    'STUDENT',
    'TEACHER',
    'ADMIN'
);

CREATE TYPE moderation_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
);

CREATE TYPE work_type AS ENUM (
    'QUALIFICATION_WORK',
    'COURSEWORK',
    'SCIENTIFIC_ARTICLE'
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
    role user_role NOT NULL,
    first_name varchar NOT NULL,
    last_name varchar NOT NULL,
    middle_name varchar NOT NULL,
    group_id uuid,
    specialty_id uuid,
    created_at timestamptz DEFAULT NOW() NOT NULL,
    updated_at timestamptz
);

CREATE TABLE scientific_works (
    id uuid PRIMARY KEY,
    title varchar NOT NULL,
    annotation text NOT NULL,
    student_id uuid NOT NULL,
    supervisor_id uuid NOT NULL,
    work_type work_type NOT NULL,
    moderation_status moderation_status DEFAULT 'PENDING',
    publication_year int NOT NULL,
    created_at timestamptz DEFAULT NOW() NOT NULL,
    updated_at timestamptz,
    published_at timestamptz
);

CREATE TABLE work_files (
    id uuid PRIMARY KEY,
    work_id uuid,
    file_name varchar NOT NULL,
    original_name varchar NOT NULL,
    file_size bigint,
    uploaded_at timestamptz DEFAULT NOW() NOT NULL
);

CREATE TABLE work_comments (
    id uuid PRIMARY KEY,
    work_id uuid NOT NULL,
    user_id uuid NOT NULL,
    comment text NOT NULL,
    created_at timestamptz DEFAULT NOW() NOT NULL
);

ALTER TABLE groups
    ADD CONSTRAINT fk_groups_specialty FOREIGN KEY (specialty_id)
        REFERENCES specialties (id) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_group FOREIGN KEY (group_id)
        REFERENCES groups (id) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE users
    ADD CONSTRAINT fk_users_specialty FOREIGN KEY (specialty_id)
        REFERENCES specialties (id) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE scientific_works
    ADD CONSTRAINT fk_works_student FOREIGN KEY (student_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE scientific_works
    ADD CONSTRAINT fk_works_supervisor FOREIGN KEY (supervisor_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE work_files
    ADD CONSTRAINT fk_files_work FOREIGN KEY (work_id)
        REFERENCES scientific_works (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE work_comments
    ADD CONSTRAINT fk_comments_work FOREIGN KEY (work_id)
        REFERENCES scientific_works (id) ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE work_comments
    ADD CONSTRAINT fk_comments_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE;