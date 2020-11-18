package de.stealwonders.epicjobs;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariDataSource;
import de.iani.playerUUIDCache.PlayerUUIDCacheAPI;
import de.stealwonders.epicjobs.commands.Commands;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobManager;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.ProjectManager;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.storage.implementation.SqlStorage;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import net.kyori.text.format.TextDecoration;
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
import java.util.stream.Collectors;

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
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.DONE)).collect(Collectors.toList());
            if (jobs.size() >= 1) {
                sendReviewerJoinMessage(player, jobs.size());
            }
        } else if (player.hasPermission("epicjobs.command.job.claim")) {
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.OPEN)).collect(Collectors.toList());
            if (jobs.size() >= 1) {
                sendPlayerJoinMessage(player, jobs.size());
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        getEpicJobsPlayer(event.getPlayer().getUniqueId()).ifPresent(epicJobsPlayer -> epicJobsPlayers.remove(epicJobsPlayer));
    }

    private void sendReviewerJoinMessage(final Player player, final int jobCount) {
        final TextComponent textComponent = TextComponent.builder()
            .content("There are ").color(TextColor.YELLOW)
            .append(TextComponent.of(jobCount).color(TextColor.GOLD))
            .append(TextComponent.of(" job(s) marked as done. Use ").color(TextColor.YELLOW))
            .append(TextComponent.of("/jobs list done").color(TextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Review jobs!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/jobs list done")))
            .append(TextComponent.of(" to review them.").color(TextColor.YELLOW))
            .build();
        TextAdapter.sendMessage(player, textComponent);
    }

    private void sendPlayerJoinMessage(final Player player, final int jobCount) {
        final TextComponent textComponent = TextComponent.builder()
            .content("There are ").color(TextColor.YELLOW)
            .append(TextComponent.of(jobCount).color(TextColor.GOLD))
            .append(TextComponent.of(" job(s) available to be claimed. Use ").color(TextColor.YELLOW))
            .append(TextComponent.of("/jobs list").color(TextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("View jobs!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/jobs list")))
            .append(TextComponent.of(" to find one for yourself.").color(TextColor.YELLOW))
            .build();
        TextAdapter.sendMessage(player, textComponent);
    }

}
