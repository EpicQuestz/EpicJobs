package de.stealwonders.epicjobs.project;

import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectManager {

    private EpicJobs plugin;

    private List<Project> projects;

    private static final String SELECT = "SELECT * FROM project;";

    public ProjectManager(EpicJobs plugin) {
        System.out.println("----- ProjectManager");
        this.plugin = plugin;
        projects = new ArrayList<>();
        fetchProjects();

        //todo debuging
        for (Project project : projects) {
            System.out.println(project.getId());
            System.out.println(project.getName());
            System.out.println(project.getLeader());
            System.out.println(project.getCreationTime());
            System.out.println(project.getLocation());
            System.out.println(project.getProjectStatus());

        }


    }

    public List<Project> getProjects() {
        return ImmutableList.copyOf(projects);
    }

    public Project getProjectById(int id) {
        for (Project project : projects) {
            if (project.getId() == id) {
                return project;
            }
        }
        return null;
    }

    public Project getProjectByName(String name) {
        for (Project project : projects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }

    public List<Project> getOpenProjects() {
        List<Project> projectList = new ArrayList<>();
        for (Project project : projects) {
            if (project.getProjectStatus() == ProjectStatus.ACTIVE) {
                projectList.add(project);
            }
        }
        return ImmutableList.copyOf(projectList);
    }

    private void fetchProjects() {

        Connection connection = null;

        try {
            connection = plugin.getHikari().getConnection();

            PreparedStatement preparedStatement = connection.prepareStatement(SELECT);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                UUID uuid = UUID.fromString(resultSet.getString("leader"));
                Timestamp creationTime = resultSet.getTimestamp("creationtime");
                Location location = Utils.deserializeLocation(resultSet.getString("location"));
                ProjectStatus projectStatus = ProjectStatus.valueOf(resultSet.getString("projectstatus"));

                Project project = new Project(id, name, uuid, creationTime.getTime(), location, projectStatus);
                projects.add(project);

            }

            resultSet.close();
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

    public int getFreeId() {
        int maxNumber = 0;
        for (Project project : projects) {
            if (project.getId() > maxNumber) {
                maxNumber = project.getId();
            }
        }
        return maxNumber + 1;
    }
}
