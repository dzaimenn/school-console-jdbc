-- CREATE USER school_manager WITH PASSWORD '1234';
--
-- CREATE DATABASE school_db;
--
-- GRANT ALL PRIVILEGES ON DATABASE school_db TO school_manager;
-- Проверка существования роли перед созданием
DO $$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'school_manager') THEN
            CREATE USER school_manager WITH PASSWORD '0017';
        END IF;
    END $$;


CREATE DATABASE school_db;

GRANT ALL PRIVILEGES ON DATABASE school_db TO school_manager;

SET search_path TO school_db, public;

CREATE SCHEMA student_management;

DROP TABLE IF EXISTS student_courses;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS courses;


CREATE TABLE groups
(
    group_id   SERIAL PRIMARY KEY,
    group_name VARCHAR(10)
);

CREATE TABLE students
(
    student_id SERIAL PRIMARY KEY,
    group_id   INT,
    first_name VARCHAR(255),
    last_name  VARCHAR(255)
);

CREATE TABLE courses
(
    course_id          SERIAL PRIMARY KEY,
    course_name        VARCHAR(128),
    course_description VARCHAR(255)
);

CREATE TABLE student_courses
(
    student_id INT REFERENCES students (student_id),
    course_id  INT REFERENCES courses (course_id),
    CONSTRAINT student_courses_pk PRIMARY KEY (student_id, course_id)
);

REVOKE ALL PRIVILEGES ON DATABASE school_db FROM school_manager;
DROP DATABASE IF EXISTS school_db;
DROP USER IF EXISTS school_manager;