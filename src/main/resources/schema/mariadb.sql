-- EpicJobs MariaDB Schema

CREATE TABLE IF NOT EXISTS project (
    id             INT AUTO_INCREMENT                    PRIMARY KEY,
    creation_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    name           VARCHAR(255)                          NOT NULL,
    leaders        LONGTEXT                              NOT NULL,
    location       VARCHAR(255)                          NOT NULL,
    project_status ENUM ('ACTIVE', 'COMPLETE')           NOT NULL,
    CONSTRAINT leaders
        CHECK (json_valid(`leaders`))
) COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS job (
    id             INT AUTO_INCREMENT                                                                    PRIMARY KEY,
    creation_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP()                                                 NOT NULL,
    update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP()                                                 NOT NULL,
    creator        VARCHAR(36)                                                                           NOT NULL,
    claimant       VARCHAR(36)                                                                           NULL,
    description    VARCHAR(255)                                                                          NOT NULL,
    project        INT                                                                                   NOT NULL,
    location       VARCHAR(255)                                                                          NOT NULL,
    job_status     ENUM ('OPEN', 'TAKEN', 'DONE', 'COMPLETE')                                            NOT NULL,
    job_category   ENUM ('TERRAIN', 'INTERIOR', 'STRUCTURE', 'NATURE', 'DECORATION', 'REMOVAL', 'OTHER') NOT NULL,
    job_difficulty ENUM ('EASY', 'MEDIUM', 'HARD')                                                       NOT NULL,
    CONSTRAINT job_ibfk_1
        FOREIGN KEY (project) REFERENCES project (id)
) COLLATE = utf8mb4_bin;

CREATE INDEX IF NOT EXISTS project
    ON job (project);
