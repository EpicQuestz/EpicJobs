package de.stealwonders.epicjobs.project;

import co.aikar.idb.DbRow;
import com.google.common.collect.ImmutableList;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.utils.Utils;
import org.bukkit.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        CompletableFuture<List<DbRow>> row = plugin.getDatabase().getResultsAsync(SELECT);
        row.whenCompleteAsync((dbRows, throwable) -> dbRows.forEach(dbRow -> {

            int id = dbRow.getInt("id");
            String name = dbRow.getString("name");
            UUID uuid = UUID.fromString(dbRow.getString("leader"));
            Timestamp creationTime = Timestamp.valueOf(dbRow.getString("creationtime"));
            Location location = Utils.deserializeLocation(dbRow.getString("location"));
            ProjectStatus projectStatus = ProjectStatus.valueOf(dbRow.getString("projectstatus"));

            Project project = new Project(id, name, uuid, creationTime.getTime(), location, projectStatus);
            projects.add(project);

        }));

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
