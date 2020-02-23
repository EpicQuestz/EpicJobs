package de.stealwonders.epicjobs.storage.implementation;

import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Location;

import java.sql.*;
import java.util.UUID;

public class SqlStorage implements StorageImplementation {

    private static final String PROJECT_SELECT_ALL = "SELECT * FROM project;";
    private static final String JOB_SELECT_ALL = "SELECT * FROM job;";

    private static final String PROJECT_INSERT = "INSERT INTO project(name, leader, location, projectstatus) VALUES (?, ?, ?, ?);";
    private static final String JOB_INSERT = "INSERT INTO job(creator, description, project, location, jobstatus, jobcategory) VALUES (?, ?, ?, ?, ?, ?);";
    private static final String GET_ID = "SELECT LAST_INSERT_ID() AS 'id';";

    private static final String PROJECT_DELETE = "DELETE FROM project WHERE id = ?;";
    private static final String JOB_DELETE = "DELETE FROM job WHERE id = ?;";

    private static final String PROJECT_TABLE_CREATE =
        "CREATE TABLE IF NOT EXISTS project (" +
        "id INT(11) COLLATE utf8_bin AUTO_INCREMENT PRIMARY KEY," +
        "name VARCHAR(255) COLLATE utf8_bin NOT NULL," +
        "leader VARCHAR(36) COLLATE utf8_bin NOT NULL," +
        "creationtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
        "location VARCHAR(255) COLLATE utf8_bin NOT NULL," +
        "projectstatus enum('ACTIVE', 'COMPLETE') NOT NULL" +
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
        "jobstatus enum('OPEN', 'TAKEN', 'DONE', 'REOPENED', 'COMPLETE') COLLATE utf8_bin NOT NULL,\n" +
        "jobcategory enum('TERRAIN', 'INTERIOR', 'STRUCTURE', 'NATURE', 'DECORATION', 'REMOVAL', 'OTHER') COLLATE utf8_bin NOT NULL" +
        ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;";
//      "CONSTRAINT job_ibfk_1" +
//      "FOREIGN KEY (project) REFERENCES project (id);" +
//      "CREATE INDEX IF NOT EXISTS project ON job (project);";

    private EpicJobs plugin;
    private HikariDataSource hikariDataSource;

    public SqlStorage(EpicJobs plugin, HikariDataSource hikariDataSource) {
        this.plugin = plugin;
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public EpicJobs getPlugin() {
        return plugin;
    }

    @Override
    public void init() {

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_TABLE_CREATE);
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(JOB_TABLE_CREATE);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        System.out.println("This does nothing: shutdown");
    }

    @Override
    public EpicJobsPlayer loadPlayer(UUID uniqueId) {
        return null;
    }

    @Override
    public Project createAndLoadProject(String name, UUID leader, Location location, ProjectStatus projectStatus) {

        Project project = null;
        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_INSERT);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, leader.toString());
            preparedStatement.setString(3, Utils.serializeLocation(location));
            preparedStatement.setString(4, projectStatus.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(GET_ID);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                project = new Project(resultSet.getInt("id"), name, leader, System.currentTimeMillis(), location, projectStatus);
            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return project;
    }

    @Override
    public Project loadProject(int id) {
        return null;
    }


    @Override
    public void loadAllProjects() {

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_SELECT_ALL);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                UUID uniqueId = UUID.fromString(resultSet.getString("leader"));
                Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                Location location = Utils.deserializeLocation(resultSet.getString("location"));
                ProjectStatus projectStatus = ProjectStatus.valueOf(resultSet.getString("projectstatus"));

                if (location != null) {
                    Project project = new Project(id, name, uniqueId, creationTime.getTime(), location, projectStatus);
                    getPlugin().getProjectManager().addProject(project);
                }
            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveProject(Project project) {

    }

    @Override
    public void deleteProject(Project project) {

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_DELETE);
            preparedStatement.setInt(1, project.getId());
            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Job createAndLoadJob(UUID creator, String description, Project project, Location location, JobStatus jobStatus, JobCategory jobCategory) {

        Job job = null;
        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(JOB_INSERT);
            preparedStatement.setString(1, creator.toString());
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, project.getId());
            preparedStatement.setString(4, Utils.serializeLocation(location));
            preparedStatement.setString(5, jobStatus.toString());
            preparedStatement.setString(6, jobCategory.toString());
            preparedStatement.execute();

            preparedStatement = connection.prepareStatement(GET_ID);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                job = new Job(resultSet.getInt("id"), creator, null, System.currentTimeMillis(), description, project, location, jobStatus, jobCategory);
            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return job;
    }

    @Override
    public Job loadJob(int id) {
        return null;
    }

    @Override
    public void loadAllJobs() {

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(JOB_SELECT_ALL);
            preparedStatement.execute();

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                UUID creator = UUID.fromString(resultSet.getString("creator"));
                UUID claimant = resultSet.getObject("claimant") != null ? UUID.fromString(resultSet.getString("claimant")) : null;
                Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                String description = resultSet.getString("description");
                Project project = plugin.getProjectManager().getProjectById(resultSet.getInt("project"));
                Location location = Utils.deserializeLocation(resultSet.getString("location"));
                JobStatus jobStatus = JobStatus.valueOf(resultSet.getString("jobstatus"));
                JobCategory jobCategory = JobCategory.valueOf(resultSet.getString("jobcategory"));

                if (location != null) {
                    Job job = new Job(id, creator, claimant, creationTime.getTime(), description, project, location, jobStatus, jobCategory);
                    getPlugin().getJobManager().addJob(job);
                }

            }

            preparedStatement.close();
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveJob(Job job) {

    }

    @Override
    public void deleteJob(Job job) {

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(JOB_DELETE);
            preparedStatement.setInt(1, job.getId());
            preparedStatement.execute();
            preparedStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
