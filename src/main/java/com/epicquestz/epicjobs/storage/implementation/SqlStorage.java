package com.epicquestz.epicjobs.storage.implementation;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.user.User;
import com.epicquestz.epicjobs.utils.Utils;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.UUID;

public record SqlStorage(EpicJobs plugin) implements StorageImplementation {

    private static final String PROJECT_SELECT_ALL = "SELECT * FROM project;";
    private static final String JOB_SELECT_ALL = "SELECT * FROM job;";
    private static final String PLAYER_SELECT_ALL = "SELECT uuid, name FROM player;";

    private static final String PLAYER_UPSERT = "INSERT INTO player(uuid, name, last_seen) VALUES (?, ?, CURRENT_TIMESTAMP) " +
        "ON DUPLICATE KEY UPDATE name = VALUES(name), last_seen = CURRENT_TIMESTAMP;";

    private static final String PROJECT_UPDATE = "UPDATE project SET name = ?, leader = ?, location = ?, projectstatus = ? WHERE id = ?;";
    private static final String JOB_UPDATE = "UPDATE job SET claimant = ?, description = ?, project = ?, location = ?, jobstatus = ?, jobcategory = ? WHERE id = ?;";

    private static final String PROJECT_INSERT = "INSERT INTO project(name, leader, location, projectstatus) VALUES (?, ?, ?, ?);";
    private static final String JOB_INSERT = "INSERT INTO job(creator, description, project, location, jobstatus, jobcategory) VALUES (?, ?, ?, ?, ?, ?);";

    private static final String PROJECT_DELETE = "DELETE FROM project WHERE id = ?;";
    private static final String JOB_DELETE = "DELETE FROM job WHERE id = ?;";
    private static final String JOB_DELETE_BY_PROJECT = "DELETE FROM job WHERE project = ?;";

    private static final String PROJECT_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS project (" +
        "id INT(11) AUTO_INCREMENT PRIMARY KEY," +
        "name VARCHAR(255) COLLATE utf8_bin NOT NULL," +
        "leader VARCHAR(36) COLLATE utf8_bin NOT NULL," +
        "creationtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
        "location VARCHAR(255) COLLATE utf8_bin NOT NULL," +
        "projectstatus enum('ACTIVE', 'PAUSED', 'COMPLETE') NOT NULL" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
//      "CREATE INDEX IF NOT EXISTS id ON project (id);";

    private static final String JOB_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS job (\n" +
        "id INT(11) AUTO_INCREMENT PRIMARY KEY,\n" +
        "creator VARCHAR(36) COLLATE utf8_bin NOT NULL,\n" +
        "claimant VARCHAR(36) COLLATE utf8_bin NULL,\n" +
        "creationtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,\n" +
        "description VARCHAR(255) COLLATE utf8_bin NOT NULL,\n" +
        "project INT NOT NULL,\n" +
        "location VARCHAR(255) COLLATE utf8_bin NOT NULL,\n" +
        "jobstatus enum('OPEN', 'TAKEN', 'DONE', 'COMPLETE') COLLATE utf8_bin NOT NULL,\n" +
        "jobcategory enum('TERRAIN', 'VEGETATION', 'PATHWAY', 'ATMOSPHERE', 'EXTERIOR_STRUCTURE', 'INTERIOR_STRUCTURE', 'INTERIOR', 'REMOVAL', 'OTHER') COLLATE utf8_bin NOT NULL" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
//      "CONSTRAINT job_ibfk_1" +
//      "FOREIGN KEY (project) REFERENCES project (id);" +
//      "CREATE INDEX IF NOT EXISTS project ON job (project);";

    private static final String PLAYER_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS player (" +
        "uuid VARCHAR(36) COLLATE utf8_bin NOT NULL PRIMARY KEY," +
        "name VARCHAR(16) COLLATE utf8_bin NOT NULL," +
        "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

    /**
     * Current database schema version. Bump this and add a corresponding case to
     * {@link #migrationStatements(int)} whenever the schema changes.
     */
    private static final int CURRENT_SCHEMA_VERSION = 1;

    private static final String SCHEMA_VERSION_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS schema_version (" +
        "version INT NOT NULL PRIMARY KEY," +
        "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";

    private static final String SCHEMA_VERSION_SELECT = "SELECT MAX(version) AS version FROM schema_version;";
    private static final String SCHEMA_VERSION_INSERT = "INSERT INTO schema_version(version) VALUES (?);";

    // Migration v1: expand job categories. Remap the removed categories to OTHER (so existing
    // rows stay loadable), then widen the enum column to the new set of values.
    private static final String[] MIGRATION_V1 = {
        "UPDATE job SET jobcategory = 'OTHER' WHERE jobcategory IN ('STRUCTURE', 'NATURE', 'DECORATION');",
        "ALTER TABLE job MODIFY COLUMN jobcategory " +
            "enum('TERRAIN', 'VEGETATION', 'PATHWAY', 'ATMOSPHERE', 'EXTERIOR_STRUCTURE', 'INTERIOR_STRUCTURE', 'INTERIOR', 'REMOVAL', 'OTHER') " +
            "COLLATE utf8_bin NOT NULL;"
    };

    @Override
    public void init() {
        try (final Connection connection = plugin.getHikariDataSource().getConnection()) {
            // Base schema for fresh installs. Existing databases already have these tables
            // (possibly at an older schema) and are brought up to date by runMigrations().
            execute(connection, SCHEMA_VERSION_TABLE_CREATE);
            execute(connection, PROJECT_TABLE_CREATE);
            execute(connection, JOB_TABLE_CREATE);
            execute(connection, PLAYER_TABLE_CREATE);

            runMigrations(connection);
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    private void runMigrations(final Connection connection) throws SQLException {
        final int current = getSchemaVersion(connection);
        for (int target = current + 1; target <= CURRENT_SCHEMA_VERSION; target++) {
            for (final String statement : migrationStatements(target)) {
                execute(connection, statement);
            }
            recordSchemaVersion(connection, target);
        }
    }

    private String[] migrationStatements(final int version) {
        return switch (version) {
            case 1 -> MIGRATION_V1;
            default -> new String[0];
        };
    }

    private int getSchemaVersion(final Connection connection) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SCHEMA_VERSION_SELECT);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                final int version = resultSet.getInt("version");
                return resultSet.wasNull() ? 0 : version;
            }
            return 0;
        }
    }

    private void recordSchemaVersion(final Connection connection, final int version) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(SCHEMA_VERSION_INSERT)) {
            preparedStatement.setInt(1, version);
            preparedStatement.execute();
        }
    }

    private void execute(final Connection connection, final String sql) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        }
    }

    @Override
    public void shutdown() {
        plugin.getHikariDataSource().close();
        System.out.println("Shutting down SQL data storage...");
    }

    @Override
    public User loadPlayer(final UUID uniqueId) {
        return null;
    }

    @Override
    public void loadAllPlayers() {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(PLAYER_SELECT_ALL);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                plugin.getPlayerCache().cache(UUID.fromString(resultSet.getString("uuid")), resultSet.getString("name"));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayer(final UUID uniqueId, final String name) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(PLAYER_UPSERT)) {
            preparedStatement.setString(1, uniqueId.toString());
            preparedStatement.setString(2, name);
            preparedStatement.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Project createAndLoadProject(final String name, final UUID leader, final Location location, final ProjectStatus projectStatus) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, leader.toString());
            preparedStatement.setString(3, Utils.serializeLocation(location));
            preparedStatement.setString(4, projectStatus.toString());
            preparedStatement.executeUpdate();

            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return new Project(resultSet.getInt(1), name, leader, System.currentTimeMillis(), location, projectStatus);
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Project loadProject(final int id) {
        return null;
    }


    @Override
    public void loadAllProjects() {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_SELECT_ALL);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final String name = resultSet.getString("name");
                final UUID uniqueId = UUID.fromString(resultSet.getString("leader"));
                final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                final Location location = Utils.deserializeLocation(resultSet.getString("location"));
                final ProjectStatus projectStatus = ProjectStatus.valueOf(resultSet.getString("projectstatus"));

                if (location != null) {
                    final Project project = new Project(id, name, uniqueId, creationTime.getTime(), location, projectStatus);
                    plugin().getProjectManager().addProject(project);
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateProject(final Project project) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_UPDATE)) {
            preparedStatement.setString(1, project.getName());
            preparedStatement.setString(2, project.getLeader().toString());
            preparedStatement.setString(3, Utils.serializeLocation(project.getLocation()));
            preparedStatement.setString(4, project.getProjectStatus().toString());
            preparedStatement.setInt(5, project.getId());
            preparedStatement.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProject(final Project project) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection()) {
            // Remove child jobs first so we never leave rows pointing at a deleted project.
            try (final PreparedStatement deleteJobs = connection.prepareStatement(JOB_DELETE_BY_PROJECT)) {
                deleteJobs.setInt(1, project.getId());
                deleteJobs.execute();
            }
            try (final PreparedStatement deleteProject = connection.prepareStatement(PROJECT_DELETE)) {
                deleteProject.setInt(1, project.getId());
                deleteProject.execute();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Job createAndLoadJob(final UUID creator, final String description, final Project project, final Location location, final JobStatus jobStatus, final JobCategory jobCategory) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(JOB_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, creator.toString());
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, project.getId());
            preparedStatement.setString(4, Utils.serializeLocation(location));
            preparedStatement.setString(5, jobStatus.toString());
            preparedStatement.setString(6, jobCategory.toString());
            preparedStatement.executeUpdate();

            try (final ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return new Job(resultSet.getInt(1), creator, null, System.currentTimeMillis(), description, project, location, jobStatus, jobCategory);
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Job loadJob(final int id) {
        return null;
    }

    @Override
    public void loadAllJobs() {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(JOB_SELECT_ALL);
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                final int id = resultSet.getInt("id");
                final UUID creator = UUID.fromString(resultSet.getString("creator"));
                final UUID claimant = resultSet.getObject("claimant") != null ? UUID.fromString(resultSet.getString("claimant")) : null;
                final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                final String description = resultSet.getString("description");
                final Project project = plugin.getProjectManager().getProjectById(resultSet.getInt("project"));
                final Location location = Utils.deserializeLocation(resultSet.getString("location"));
                final JobStatus jobStatus = JobStatus.valueOf(resultSet.getString("jobstatus"));
                final JobCategory jobCategory = JobCategory.valueOf(resultSet.getString("jobcategory"));

                // Skip jobs whose project or world is missing - they would only cause NPEs later.
                if (project == null || location == null) {
                    continue;
                }

                final Job job = new Job(id, creator, claimant, creationTime.getTime(), description, project, location, jobStatus, jobCategory);
                plugin().getJobManager().addJob(job);
                project.addJob(job);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateJob(final Job job) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(JOB_UPDATE)) {
            if (job.getClaimant() == null) {
                preparedStatement.setNull(1, Types.VARCHAR);
            } else {
                preparedStatement.setString(1, job.getClaimant().toString());
            }
            preparedStatement.setString(2, job.getDescription());
            preparedStatement.setInt(3, job.getProject().getId());
            preparedStatement.setString(4, Utils.serializeLocation(job.getLocation()));
            preparedStatement.setString(5, job.getJobStatus().toString());
            preparedStatement.setString(6, job.getJobCategory().toString());
            preparedStatement.setInt(7, job.getId());
            preparedStatement.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteJob(final Job job) {
        try (final Connection connection = plugin.getHikariDataSource().getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(JOB_DELETE)) {
            preparedStatement.setInt(1, job.getId());
            preparedStatement.execute();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

}
