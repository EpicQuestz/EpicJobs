//package de.stealwonders.epicjobs.storage.implementation;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import de.stealwonders.epicjobs.EpicJobs;
//import de.stealwonders.epicjobs.model.job.Job;
//import de.stealwonders.epicjobs.model.job.JobCategory;
//import de.stealwonders.epicjobs.model.job.JobStatus;
//import de.stealwonders.epicjobs.model.project.Project;
//import de.stealwonders.epicjobs.model.project.ProjectStatus;
//import de.stealwonders.epicjobs.storage.SchemaReader;
//import de.stealwonders.epicjobs.utils.Utils;
//import org.bukkit.Location;
//import org.bukkit.entity.Player;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.sql.Timestamp;
//import java.sql.Types;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class SqlStorage implements StorageImplementation {
//
//    private static final String PROJECT_SELECT_ALL = "SELECT * FROM project;";
//    private static final String JOB_SELECT_ALL = "SELECT * FROM job;";
//
//    private static final String PROJECT_UPDATE = "UPDATE project SET name = ?, leaders = ?, location = ?, projectstatus = ? WHERE id = ?;";
//    private static final String JOB_UPDATE = "UPDATE job SET claimant = ?, updatetime = NOW(), description = ?, project = ?, location = ?, jobstatus = ?, jobcategory = ? WHERE id = ?;";
//
//    private static final String PROJECT_INSERT = "INSERT INTO project(name, leaders, location, projectstatus) VALUES (?, ?, ?, ?);";
//    private static final String JOB_INSERT = "INSERT INTO job(creator, description, project, location, jobstatus, jobcategory) VALUES (?, ?, ?, ?, ?, ?);";
//    private static final String GET_ID = "SELECT LAST_INSERT_ID() AS 'id';";
//
//    private static final String PROJECT_DELETE = "DELETE FROM project WHERE id = ?;";
//    private static final String JOB_DELETE = "DELETE FROM job WHERE id = ?;";
//
//    private final EpicJobs plugin;
//
//    public SqlStorage(final EpicJobs plugin) {
//        this.plugin = plugin;
//    }
//
//    @Override
//    public EpicJobs getPlugin() {
//        return plugin;
//    }
//
//    @Override
//    public void init() {
//
//        try {
//            applySchema();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//
////        Connection connection = null;
////
////        try {
////            connection = plugin.getHikariDataSource().getConnection();
////
////            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_TABLE_CREATE);
////            preparedStatement.execute();
////
////            preparedStatement = connection.prepareStatement(JOB_TABLE_CREATE);
////            preparedStatement.execute();
////            preparedStatement.close();
////        } catch (final SQLException e) {
////            e.printStackTrace();
////        } finally {
////            if (connection != null) {
////                try {
////                    connection.close();
////                } catch (final SQLException e) {
////                    e.printStackTrace();
////                }
////            }
////        }
//
//    }
//
//    private void applySchema() throws IOException, SQLException {
//        List<String> statements;
//        try (InputStream inputStream = this.plugin.getResource("database_schema.sql")) {
//            if (inputStream == null) {
//                throw new IOException("Couldn't locate schema file for database.");
//            }
//            statements = new ArrayList<>(SchemaReader.getStatements(inputStream));
//        }
//
//        try (Connection connection = plugin.getHikariDataSource().getConnection()) {
//            try (Statement statement = connection.createStatement()) {
//                for (String query : statements) {
//                    statement.addBatch(query);
//                }
//                statement.executeBatch();
//            }
//        }
//    }
//
//    @Override
//    public void shutdown() {
//        plugin.getHikariDataSource().close();
//        System.out.println("Shutting down SQL data storage...");
//    }
//
//    @Override
//    public Project createAndLoadProject(final String name, final Player leader) {
//
//        Project project = null;
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
////            JsonObject leaders = new JsonObject();
//            JsonArray leaders = new JsonArray();
//            leaders.add(leader.getUniqueId().toString());
//
//            PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_INSERT);
//            preparedStatement.setString(1, name);
//            preparedStatement.setString(2, leaders.getAsString());
//            preparedStatement.setString(3, Utils.serializeLocation(leader.getLocation()));
//            preparedStatement.setString(4, ProjectStatus.ACTIVE.toString());
//            preparedStatement.execute();
//
//            preparedStatement = connection.prepareStatement(GET_ID);
//            preparedStatement.execute();
//
//            final ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                project = new Project(resultSet.getInt("id"), name, leader);
//            }
//
//            preparedStatement.close();
//            resultSet.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return project;
//    }
//
//    @Override
//    public List<Project> loadAllProjects() {
//
//        List<Project> projects = new ArrayList<>();
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_SELECT_ALL);
//            preparedStatement.execute();
//
//            final ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                final int id = resultSet.getInt("id");
//                final String name = resultSet.getString("name");
//                final String jsonLeaders = resultSet.getString("leaders");
//                final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
//                final Timestamp updateTime = Timestamp.valueOf(resultSet.getString("updatetime"));
//                final Location location = Utils.deserializeLocation(resultSet.getString("location"));
//                final ProjectStatus projectStatus = ProjectStatus.valueOf(resultSet.getString("projectstatus"));
//
//                Gson gson = new Gson();
//                String[] stringLeaders = gson.fromJson(jsonLeaders, String[].class);
//                List<UUID> leaders = new ArrayList<>();
//                for (String string : stringLeaders) {
//                    UUID uuid = UUID.fromString(string);
//                    leaders.add(uuid);
//                }
//
//                if (location != null) {
//                    final Project project = new Project(id, name, leaders, creationTime.getTime(), updateTime.getTime(), location, projectStatus);
//                    projects.add(project);
//                }
//            }
//
//            preparedStatement.close();
//            resultSet.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return projects;
//    }
//
//    @Override
//    public void updateProject(final Project project) {
//
//        Connection connection = null;
//
//        JsonArray leaders = new JsonArray();
//        project.getLeaders().forEach(leader -> leaders.add(leader.toString()));
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_UPDATE);
//            preparedStatement.setString(1, project.getName());
//            preparedStatement.setString(2, leaders.getAsString());
//            preparedStatement.setString(3, Utils.serializeLocation(project.getLocation()));
//            preparedStatement.setString(4, project.getProjectStatus().toString());
//            preparedStatement.setInt(5, project.getId());
//            preparedStatement.execute();
//            preparedStatement.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // ---------------- done ---------------------------------------
//    @Override
//    public void deleteProject(final Project project) {
//
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(PROJECT_DELETE);
//            preparedStatement.setInt(1, project.getId());
//            preparedStatement.execute();
//            preparedStatement.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // ---------------- done ---------------------------------------
//    @Override
//    public Job createAndLoadJob(Player creator, String description, Project project, JobCategory jobCategory) {
//
//        Job job = null;
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            PreparedStatement preparedStatement = connection.prepareStatement(JOB_INSERT);
//            preparedStatement.setString(1, creator.toString());
//            preparedStatement.setString(2, description);
//            preparedStatement.setInt(3, project.getId());
//            preparedStatement.setString(4, Utils.serializeLocation(creator.getLocation()));
//            preparedStatement.setString(5, JobStatus.OPEN.toString());
//            preparedStatement.setString(6, jobCategory.toString());
//            preparedStatement.execute();
//
//            preparedStatement = connection.prepareStatement(GET_ID);
//            preparedStatement.execute();
//
//            final ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                job = new Job(resultSet.getInt("id"), creator, description, project, jobCategory);
//            }
//
//            preparedStatement.close();
//            resultSet.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return job;
//    }
//
//    // ---------------- done ---------------------------------------
//    @Override
//    public List<Job> loadAllJobs() {
//
//        List<Job> jobs = new ArrayList<>();
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(JOB_SELECT_ALL);
//            preparedStatement.execute();
//
//            final ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//
//                final int id = resultSet.getInt("id");
//                final UUID creator = UUID.fromString(resultSet.getString("creator"));
//                final UUID claimant = resultSet.getObject("claimant") != null ? UUID.fromString(resultSet.getString("claimant")) : null;
//                final Timestamp creationTime = Timestamp.valueOf(resultSet.getString("creationtime"));
//                final Timestamp updateTime = Timestamp.valueOf(resultSet.getString("updateTime"));
//                final String description = resultSet.getString("description");
//                final Project project = plugin.getProjectManager().getProjectById(resultSet.getInt("project"));
//                final Location location = Utils.deserializeLocation(resultSet.getString("location"));
//                final JobStatus jobStatus = JobStatus.valueOf(resultSet.getString("jobstatus"));
//                final JobCategory jobCategory = JobCategory.valueOf(resultSet.getString("jobcategory"));
//
//                if (location != null) {
//                    final Job job = new Job(id, creator, claimant, creationTime.getTime(), updateTime.getTime(), description, project, location, jobStatus, jobCategory);
//                    jobs.add(job);
//                }
//
//            }
//
//            preparedStatement.close();
//            resultSet.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return jobs;
//    }
//
//    // ---------------- done ---------------------------------------
//    @Override
//    public void updateJob(final Job job) {
//
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(JOB_UPDATE);
//            if (job.getClaimant() == null) {
//                preparedStatement.setNull(1, Types.VARCHAR);
//            } else {
//                preparedStatement.setString(1, job.getClaimant().toString());
//            }
//            preparedStatement.setString(2, job.getDescription());
//            preparedStatement.setInt(3, job.getProject().getId());
//            preparedStatement.setString(4, Utils.serializeLocation(job.getLocation()));
//            preparedStatement.setString(5, job.getJobStatus().toString());
//            preparedStatement.setString(6, job.getJobCategory().toString());
//            preparedStatement.setInt(7, job.getId());
//            preparedStatement.execute();
//            preparedStatement.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    // ---------------- done ---------------------------------------
//    @Override
//    public void deleteJob(final Job job) {
//
//        Connection connection = null;
//
//        try {
//            connection = plugin.getHikariDataSource().getConnection();
//
//            final PreparedStatement preparedStatement = connection.prepareStatement(JOB_DELETE);
//            preparedStatement.setInt(1, job.getId());
//            preparedStatement.execute();
//            preparedStatement.close();
//
//        } catch (final SQLException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//}
