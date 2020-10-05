-- EpicJobs MariaDB Schema

CREATE TABLE IF NOT EXISTS project (
    id            INT AUTO_INCREMENT                    PRIMARY KEY,
    name          VARCHAR(255)                          NOT NULL,
    leaders       LONGTEXT                              NOT NULL,
    creationtime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    updatetime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP() NOT NULL,
    location      VARCHAR(255)                          NOT NULL,
    projectstatus ENUM ('ACTIVE', 'COMPLETE')           NOT NULL,
    CONSTRAINT leaders
        CHECK (json_valid(`leaders`))
) COLLATE = utf8mb4_bin;

CREATE TABLE IF NOT EXISTS job (
    id           INT AUTO_INCREMENT                                                                    PRIMARY KEY,
    creator      VARCHAR(36)                                                                           NOT NULL,
    claimant     VARCHAR(36)                                                                           NULL,
    creationtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP()                                                 NOT NULL,
    updatetime   TIMESTAMP DEFAULT CURRENT_TIMESTAMP()                                                 NOT NULL,
    description  VARCHAR(255)                                                                          NOT NULL,
    project      INT                                                                                   NOT NULL,
    location     VARCHAR(255)                                                                          NOT NULL,
    jobstatus    ENUM ('OPEN', 'TAKEN', 'DONE', 'COMPLETE')                                            NOT NULL,
    jobcategory  ENUM ('TERRAIN', 'INTERIOR', 'STRUCTURE', 'NATURE', 'DECORATION', 'REMOVAL', 'OTHER') NOT NULL,
    CONSTRAINT job_ibfk_1
        FOREIGN KEY (project) REFERENCES project (id)
) COLLATE = utf8mb4_bin;

CREATE INDEX IF NOT EXISTS project
    ON job (project);

