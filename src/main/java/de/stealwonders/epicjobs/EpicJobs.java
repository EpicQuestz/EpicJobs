package de.stealwonders.epicjobs;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import co.aikar.commands.PaperCommandManager;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.commands.JobCommand;
import de.stealwonders.epicjobs.commands.ProjectCommand;
import de.stealwonders.epicjobs.constants.Messages;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobManager;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectManager;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.storage.implementation.SqlStorage;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public final class EpicJobs extends JavaPlugin implements Listener {

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private SettingsFile settingsFile;

    private HikariDataSource hikariDataSource = new HikariDataSource();
    private StorageImplementation storageImplementation;

    private Set<EpicJobsPlayer> epicJobsPlayers;

    private ProjectManager projectManager;
    private JobManager jobManager;

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        taskChainFactory = BukkitTaskChainFactory.create(this);

        settingsFile = new SettingsFile(this);

        storageImplementation = new SqlStorage(this, settingsFile.setupHikari(hikariDataSource, settingsFile.getConfiguration()));
        storageImplementation.init();

        epicJobsPlayers = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> epicJobsPlayers.add(new EpicJobsPlayer(player.getUniqueId())));

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);

        commandManager = new PaperCommandManager(this);

        registerCommandContexts();
        registerCommandCompletion();
        registerCommands();
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        storageImplementation.shutdown();

        Bukkit.getOnlinePlayers().forEach(player -> epicJobsPlayers.remove(getEpicJobsPlayer(player.getUniqueId())));
    }

    //todo put into other class
    private void registerCommandContexts() {
        commandManager.getCommandContexts().registerContext(Job.class, c -> {
            String number = c.popFirstArg();
            try {
                int id = Integer.parseInt(number);
                Job job = jobManager.getJobById(id);
                if (job != null) {
                    return job;
                } else {
                    throw new InvalidCommandArgument(Messages.JOB_DOESNT_EXIST.toString(), false);
                }
            } catch (NumberFormatException e) {
                throw new InvalidCommandArgument(MessageKeys.MUST_BE_A_NUMBER, "{num}", number);
            }
        });

        commandManager.getCommandContexts().registerContext(Project.class, c -> {
            Project project = getProjectManager().getProjectByName(c.popFirstArg());
            if (project != null) {
                return project;
            } else {
                throw new InvalidCommandArgument(Messages.PROJECT_DOESNT_EXIST.toString(), false);
            }
        });
    }

    //todo put into other class
    private void registerCommandCompletion() {
        commandManager.getCommandCompletions().registerAsyncCompletion("open-job", c -> {
            List<String> jobs = new ArrayList<>();
            jobManager.getOpenJobs().forEach(job -> jobs.add(String.valueOf(job.getId())));
            return jobs;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("player-job", c -> {
            List<String> jobs = new ArrayList<>();
            Player player = c.getPlayer();
            EpicJobsPlayer epicJobsPlayer = getEpicJobsPlayer(player.getUniqueId());
            epicJobsPlayer.getJobs().forEach(job -> jobs.add(String.valueOf(job.getId())));
            return jobs;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("project", c -> {
            List<String> projects = new ArrayList<>();
            projectManager.getProjects().forEach(project -> projects.add(project.getName()));
            return projects;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("active-project", c -> {
            List<String> projects;
            projects = projectManager.getProjects().stream()
                    .filter(project -> project.getProjectStatus() == ProjectStatus.ACTIVE)
                    .map(Project::getName)
                    .collect(Collectors.toList());
            return projects;
        });
    }

    private void registerCommands() {
        commandManager.registerCommand(new JobCommand(this));
        commandManager.registerCommand(new ProjectCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public StorageImplementation getStorageImplementation() {
        return storageImplementation;
    }

    public SettingsFile getSettingsFile() {
        return settingsFile;
    }

    public EpicJobsPlayer getEpicJobsPlayer(UUID uuid) {
        for (EpicJobsPlayer epicJobsPlayer : epicJobsPlayers) {
            if (epicJobsPlayer.getUuid() == uuid) {
                return epicJobsPlayer;
            }
        }
        return null;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        epicJobsPlayers.add(new EpicJobsPlayer(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        epicJobsPlayers.remove(getEpicJobsPlayer(event.getPlayer().getUniqueId()));
    }

}
