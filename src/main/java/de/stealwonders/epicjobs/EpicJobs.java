package de.stealwonders.epicjobs;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariDataSource;
import de.iani.playerUUIDCache.PlayerUUIDCacheAPI;
import de.stealwonders.epicjobs.commands.Commands;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobManager;
import de.stealwonders.epicjobs.project.ProjectManager;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.storage.implementation.SqlStorage;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class EpicJobs extends JavaPlugin implements Listener {

    private static PlayerUUIDCacheAPI playerUuidCache;

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newSharedChain(final String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private SettingsFile settingsFile;

    private ProjectManager projectManager;
    private JobManager jobManager;

    private HikariDataSource hikariDataSource;
    private StorageImplementation storageImplementation;

    private Commands commands;
    private Set<EpicJobsPlayer> epicJobsPlayers;

    @Override
    public void onEnable() {
        // Plugin startup logic

        playerUuidCache = getServer().getServicesManager().load(PlayerUUIDCacheAPI.class);

        taskChainFactory = BukkitTaskChainFactory.create(this);

        settingsFile = new SettingsFile(this);

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);

        hikariDataSource = settingsFile.setupHikari(new HikariDataSource(), settingsFile.getConfiguration());
        storageImplementation = new SqlStorage(this, hikariDataSource);
        storageImplementation.init();
        projectManager.firstLoad();
        jobManager.firstLoad();

        commands = new Commands(this);
        epicJobsPlayers = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            final EpicJobsPlayer epicJobsPlayer = new EpicJobsPlayer(player.getUniqueId());
            loadPlayerJobs(epicJobsPlayer);
            epicJobsPlayers.add(epicJobsPlayer);
        });

        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        storageImplementation.shutdown();

        Bukkit.getOnlinePlayers().forEach(player -> getEpicJobsPlayer(player.getUniqueId()).ifPresent(epicJobsPlayer -> epicJobsPlayers.remove(epicJobsPlayer)));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public static @Nullable PlayerUUIDCacheAPI getPlayerUuidCache() {
        return playerUuidCache;
    }

    public StorageImplementation getStorageImplementation() {
        return storageImplementation;
    }

    public SettingsFile getSettingsFile() {
        return settingsFile;
    }

    public Optional<EpicJobsPlayer> getEpicJobsPlayer(final UUID uuid) {
        for (final EpicJobsPlayer epicJobsPlayer : epicJobsPlayers) {
            if (epicJobsPlayer.getUuid().equals(uuid)) {
                return Optional.of(epicJobsPlayer);
            }
        }
        return Optional.empty();
    }

    private void loadPlayerJobs(final EpicJobsPlayer epicJobsPlayer) {
        for (final Job job : jobManager.getJobs()) {
            if (job.getClaimant() != null) {
                if (job.getClaimant().equals(epicJobsPlayer.getUuid())) {
                    epicJobsPlayer.addJob(job);
                }
            }
        }
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final EpicJobsPlayer epicJobsPlayer = new EpicJobsPlayer(event.getPlayer().getUniqueId());
        loadPlayerJobs(epicJobsPlayer);
        epicJobsPlayers.add(epicJobsPlayer);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        getEpicJobsPlayer(event.getPlayer().getUniqueId()).ifPresent(epicJobsPlayer -> epicJobsPlayers.remove(epicJobsPlayer));
    }

}
