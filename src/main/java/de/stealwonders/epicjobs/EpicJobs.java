package de.stealwonders.epicjobs;

import co.aikar.commands.PaperCommandManager;
import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.commands.JobCommand;
import de.stealwonders.epicjobs.job.JobManager;
import de.stealwonders.epicjobs.project.ProjectManager;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class EpicJobs extends JavaPlugin implements Listener {

    private HikariDataSource hikari = new HikariDataSource();

    private SettingsFile settingsFile;

    private Set<EpicJobsPlayer> epicJobsPlayers;

    private ProjectManager projectManager;
    private JobManager jobManager;

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        settingsFile = new SettingsFile(this);
        settingsFile.setupHikari(hikari, settingsFile.getConfiguration());

        epicJobsPlayers = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> epicJobsPlayers.add(new EpicJobsPlayer(player.getUniqueId())));

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);

        commandManager = new PaperCommandManager(this);

        registerCommands();
        registerListeners();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        Bukkit.getOnlinePlayers().forEach(player -> epicJobsPlayers.remove(getEpicJobsPlayer(player.getUniqueId())));
    }

    private void registerCommands() {
        commandManager.registerCommand(new JobCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        epicJobsPlayers.add(new EpicJobsPlayer(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        epicJobsPlayers.remove(getEpicJobsPlayer(event.getPlayer().getUniqueId()));
    }


}
