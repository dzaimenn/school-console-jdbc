CREATE USER school_manager WITH PASSWORD '1234';

CREATE DATABASE school_db;
GRANT ALL PRIVILEGES ON DATABASE school_db TO school_manager;

CREATE SCHEMA IF NOT EXISTS school_management;

GRANT ALL PRIVILEGES ON SCHEMA school_management TO school_manager;
