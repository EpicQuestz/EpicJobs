package com.epicquestz.epicjobs;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariDataSource;
import de.iani.playerUUIDCache.PlayerUUIDCacheAPI;
import com.epicquestz.epicjobs.commands.Commands;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobManager;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.ProjectManager;
import com.epicquestz.epicjobs.storage.SettingsFile;
import com.epicquestz.epicjobs.storage.implementation.SqlStorage;
import com.epicquestz.epicjobs.storage.implementation.StorageImplementation;
import com.epicquestz.epicjobs.user.EpicJobsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.List;
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

        hikariDataSource = new HikariDataSource();
        settingsFile.setupHikari(hikariDataSource, settingsFile.getConfiguration());
        storageImplementation = new SqlStorage(this);
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

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
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
        final Player player = event.getPlayer();
        final EpicJobsPlayer epicJobsPlayer = new EpicJobsPlayer(player.getUniqueId());
        loadPlayerJobs(epicJobsPlayer);
        epicJobsPlayers.add(epicJobsPlayer);
        if (player.hasPermission("epicjobs.command.job.list.done")) {
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.DONE)).toList();
            if (!jobs.isEmpty()) {
                sendReviewerJoinMessage(player, jobs.size());
            }
        } else if (player.hasPermission("epicjobs.command.job.claim")) {
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.OPEN)).toList();
            if (!jobs.isEmpty()) {
                sendPlayerJoinMessage(player, jobs.size());
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        getEpicJobsPlayer(event.getPlayer().getUniqueId()).ifPresent(epicJobsPlayer -> epicJobsPlayers.remove(epicJobsPlayer));
    }

    private void sendReviewerJoinMessage(final Player player, final int jobCount) {
        final Component textComponent = Component.text()
            .content("There are ").color(NamedTextColor.YELLOW)
            .append(Component.text(jobCount, NamedTextColor.GOLD))
            .append(Component.text(" job(s) marked as done. Use ", NamedTextColor.YELLOW))
            .append(Component.text("/jobs list done", NamedTextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("Review jobs!"))).clickEvent(ClickEvent.runCommand("/jobs list done")))
            .append(Component.text(" to review them.", NamedTextColor.YELLOW))
            .build();
        player.sendMessage(textComponent);
    }

    private void sendPlayerJoinMessage(final Player player, final int jobCount) {
        final Component textComponent = Component.text()
            .content("There are ").color(NamedTextColor.YELLOW)
            .append(Component.text(jobCount, NamedTextColor.GOLD))
            .append(Component.text(" job(s) available to be claimed. Use ", NamedTextColor.YELLOW))
            .append(Component.text("/jobs list", NamedTextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("View jobs!"))).clickEvent(ClickEvent.runCommand("/jobs list")))
            .append(Component.text(" to find one for yourself.", NamedTextColor.YELLOW))
            .build();
        player.sendMessage(textComponent);
    }

}
