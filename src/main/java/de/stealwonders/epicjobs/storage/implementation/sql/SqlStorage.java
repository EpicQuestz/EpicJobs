package de.stealwonders.epicjobs.storage.implementation.sql;

import com.google.gson.Gson;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobCategory;
import de.stealwonders.epicjobs.model.job.JobDifficulty;
import de.stealwonders.epicjobs.model.job.JobStatus;
import de.stealwonders.epicjobs.model.project.Project;
import de.stealwonders.epicjobs.model.project.ProjectStatus;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.storage.implementation.sql.connection.ConnectionFactory;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class SqlStorage implements StorageImplementation {

    private static final String PROJECT_SELECT_ALL = "SELECT * FROM project;";
    private static final String JOB_SELECT_ALL = "SELECT * FROM job;";

    private static final String PROJECT_UPDATE = "UPDATE project SET name = ?, leaders = ?, location = ?, projectstatus = ? WHERE id = ?;";
    private static final String JOB_UPDATE = "UPDATE job SET claimant = ?, updatetime = NOW(), description = ?, project = ?, location = ?, jobstatus = ?, jobcategory = ?, jobdifficulty = ? WHERE id = ?;";

    private static final String PROJECT_INSERT = "INSERT INTO project(name, leaders, location, projectstatus) VALUES (?, ?, ?, ?);";
    private static final String JOB_INSERT = "INSERT INTO job(creator, description, project, location, jobstatus, jobcategory, jobdifficulty) VALUES (?, ?, ?, ?, ?, ?, ?);";
    private static final String GET_ID = "SELECT LAST_INSERT_ID() AS 'id';";

    private static final String PROJECT_DELETE = "DELETE FROM project WHERE id = ?;";
    private static final String JOB_DELETE = "DELETE FROM job WHERE id = ?;";

    private final EpicJobs plugin;
    private final ConnectionFactory connectionFactory;

    public SqlStorage(final EpicJobs plugin, final ConnectionFactory connectionFactory) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public EpicJobs getPlugin() {
        return plugin;
    }

    @Override
    public String getImplementationName() {
        return connectionFactory.getImplementationName();
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    @Override
    public void init() throws Exception {
        connectionFactory.init(plugin);
        final boolean tableExists;
        try (final Connection connection = connectionFactory.getConnection()) {
            tableExists = tableExists(connection, "jobs") && tableExists(connection, "projects");
        }
        if (!tableExists) {
            applySchema();
        }
    }

    private void applySchema() throws IOException, SQLException {
        final List<String> statements;
        try (final InputStream inputStream = plugin.getResource("database_schema.sql")) {
            if (inputStream == null) {
                throw new IOException("Couldn't locate schema file for database");
            }
            statements = new ArrayList<>(SchemaReader.getStatements(inputStream));
        }

        try (final Connection connection = connectionFactory.getConnection()) {
            try (final Statement statement = connection.createStatement()) {
                for (final String query : statements) {
                    statement.addBatch(query);
                }
                statement.executeBatch();
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            connectionFactory.shutdown();
        } catch (final Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Exception whilst disabling SQL storage", e);
        }
    }

    @Override
    public Project createAndLoadProject(final String name, final Player leader) throws SQLException {
        Project project = null;

        final Gson gson = new Gson();
        final String leaders = gson.toJson(List.of(leader.getUniqueId()));

        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_INSERT)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, leaders);
                preparedStatement.setString(3, Utils.serializeLocation(leader.getLocation()));
                preparedStatement.setString(4, ProjectStatus.ACTIVE.toString());
                preparedStatement.execute();
            }

            try (final PreparedStatement preparedStatement = connection.prepareStatement(GET_ID)) {
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        project = new Project(resultSet.getInt("id"), name, leader);
                    }
                }
            }
        }

        return project;
    }

    @Override
    public List<Project> loadAllProjects() throws SQLException {
        final List<Project> projects = new ArrayList<>();

        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_SELECT_ALL)) {
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        final int id = resultSet.getInt("id");
                        final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                        final Timestamp updateTime = Timestamp.valueOf(resultSet.getString("updatetime"));
                        final String name = resultSet.getString("name");
                        final String jsonLeaders = resultSet.getString("leaders");
                        final Location location = Utils.deserializeLocation(resultSet.getString("location"));
                        final ProjectStatus projectStatus = ProjectStatus.valueOf(resultSet.getString("projectstatus"));

                        final Gson gson = new Gson();
                        final String[] stringLeaders = gson.fromJson(jsonLeaders, String[].class);
                        final List<UUID> leaders = new ArrayList<>();
                        for (final String string : stringLeaders) {
                            final UUID uuid = UUID.fromString(string);
                            leaders.add(uuid);
                        }

                        if (leaders.size() > 0 && location != null) {
                            final Project project = new Project(id, creationTime.getTime(), updateTime.getTime(), name, leaders, location, projectStatus);
                            projects.add(project);
                        }
                    }
                }
            }
        }

        return projects;
    }

    @Override
    public void saveProject(final Project project) throws SQLException {
        final Gson gson = new Gson();
        final String leaders = gson.toJson(List.of(project.getLeaders().stream().map(UUID::toString).collect(Collectors.toList())));
        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_UPDATE)) {
                preparedStatement.setString(1, project.getName());
                preparedStatement.setString(2, leaders);
                preparedStatement.setString(3, Utils.serializeLocation(project.getLocation()));
                preparedStatement.setString(4, project.getProjectStatus().toString());
                preparedStatement.setInt(5, project.getId());
                preparedStatement.execute();
            }
        }
    }

    @Override
    public void deleteProject(final Project project) throws SQLException {
        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_DELETE)) {
                preparedStatement.setInt(1, project.getId());
                preparedStatement.execute();
            }
        }
    }

    @Override
    public Job createAndLoadJob(final Player creator, final String description, final Project project, final JobCategory jobCategory, final JobDifficulty jobDifficulty) throws SQLException {
        Job job = null;

        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(JOB_INSERT)) {
                preparedStatement.setString(1, creator.toString());
                preparedStatement.setString(2, description);
                preparedStatement.setInt(3, project.getId());
                preparedStatement.setString(4, Utils.serializeLocation(creator.getLocation()));
                preparedStatement.setString(5, JobStatus.OPEN.toString());
                preparedStatement.setString(6, jobCategory.toString());
                preparedStatement.setString(7, jobDifficulty.toString());
                preparedStatement.execute();
            }

            try (final PreparedStatement preparedStatement = connection.prepareStatement(GET_ID)) {
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        job = new Job(resultSet.getInt("id"), creator, description, project, jobCategory, jobDifficulty);
                    }
                }
            }
        }

        return job;
    }

    @Override
    public List<Job> loadAllJobs() throws SQLException {
        final List<Job> jobs = new ArrayList<>();

        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(JOB_SELECT_ALL)) {
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        final int id = resultSet.getInt("id");
                        final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
                        final Timestamp updateTime = Timestamp.valueOf(resultSet.getString("updateTime"));
                        final UUID creator = UUID.fromString(resultSet.getString("creator"));
                        final UUID claimant = resultSet.getObject("claimant") != null ? UUID.fromString(resultSet.getString("claimant")) : null;
                        final String description = resultSet.getString("description");
                        final Project project = plugin.getProjectManager().getProjectById(resultSet.getInt("project"));
                        final Location location = Utils.deserializeLocation(resultSet.getString("location"));
                        final JobStatus jobStatus = JobStatus.valueOf(resultSet.getString("jobstatus"));
                        final JobCategory jobCategory = JobCategory.valueOf(resultSet.getString("jobcategory"));
                        final JobDifficulty jobDifficulty = JobDifficulty.valueOf(resultSet.getString("jobdifficulty"));

                        if (project != null && location != null) {
                            final Job job = new Job(id, creationTime.getTime(), updateTime.getTime(), creator, claimant, description, project, location, jobStatus, jobCategory, jobDifficulty);
                            jobs.add(job);
                        }
                    }
                }
            }
        }

        return jobs;
    }

    @Override
    public void saveJob(final Job job) throws SQLException {
        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(JOB_UPDATE)) {
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
                preparedStatement.setString(7, job.getJobDifficulty().toString());
                preparedStatement.setInt(8, job.getId());
                preparedStatement.execute();
            }
        }
    }

    @Override
    public void deleteJob(final Job job) throws SQLException {
        try (final Connection connection = connectionFactory.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement(JOB_DELETE)) {
                preparedStatement.setInt(1, job.getId());
                preparedStatement.execute();
            }
        }
    }

    private static boolean tableExists (final Connection connection, final String table) throws SQLException {
        try (final ResultSet resultSet = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null)) {
            while (resultSet.next()) {
                if (resultSet.getString(3).equalsIgnoreCase(table)) {
                    return true;
                }
            }
            return false;
        }
    }

}
