package de.stealwonders.epicjobs;

import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.commands.CommandManager;
import de.stealwonders.epicjobs.job.JobManager;
import de.stealwonders.epicjobs.project.ProjectManager;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EpicJobs extends JavaPlugin {

    private HikariDataSource hikari = new HikariDataSource();

    private SettingsFile settingsFile;

    private Set<EpicJobsPlayer> epicJobsPlayers;

    private ProjectManager projectManager;
    private JobManager jobManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        settingsFile = new SettingsFile(this);
        settingsFile.setupHikari(hikari, settingsFile.getConfiguration());

        epicJobsPlayers = new HashSet<>();

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);
        commandManager = new CommandManager(this);

        registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        this.getCommand("job").setExecutor(commandManager);
    }

    public HikariDataSource getHikari() {
        return hikari;
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
