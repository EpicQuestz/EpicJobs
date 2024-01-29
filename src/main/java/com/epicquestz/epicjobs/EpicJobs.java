package com.epicquestz.epicjobs;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.command.Commands;
import com.zaxxer.hikari.HikariDataSource;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobManager;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.ProjectManager;
import com.epicquestz.epicjobs.storage.SettingsFile;
import com.epicquestz.epicjobs.storage.implementation.SqlStorage;
import com.epicquestz.epicjobs.storage.implementation.StorageImplementation;
import com.epicquestz.epicjobs.user.User;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class EpicJobs extends JavaPlugin implements Listener {

    private static EpicJobs instance;

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newSharedChain(final String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private SettingsFile settingsFile;

    private ProjectManager projectManager;
    private JobManager jobManager;

    private HikariDataSource hikariDataSource;
    private StorageImplementation storage;

    private Commands commands;
    private Set<User> users;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        taskChainFactory = BukkitTaskChainFactory.create(this);

        settingsFile = new SettingsFile(this);

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);

        hikariDataSource = new HikariDataSource();
        settingsFile.setupHikari(hikariDataSource, settingsFile.getConfiguration());
        storage = new SqlStorage(this);
        storage.init();
        projectManager.firstLoad();
        jobManager.firstLoad();

        commands = new Commands(this);
        users = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            final User user = new User(player.getUniqueId());
            loadPlayerJobs(user);
            users.add(user);
        });

        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        storage.shutdown();

        Bukkit.getOnlinePlayers().forEach(player -> getEpicJobsPlayer(player.getUniqueId()).ifPresent(epicJobsPlayer -> users.remove(epicJobsPlayer)));
    }

    public static EpicJobs get() {
        return instance;
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public StorageImplementation getStorage() {
        return storage;
    }

    public SettingsFile getSettingsFile() {
        return settingsFile;
    }

    public Optional<User> getEpicJobsPlayer(final Player player) {
        return getEpicJobsPlayer(player.getUniqueId());
    }

    public Optional<User> getEpicJobsPlayer(final UUID uuid) {
        for (final User user : users) {
            if (user.getUuid().equals(uuid)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    private void loadPlayerJobs(final User user) {
        for (final Job job : jobManager.getJobs()) {
            if (job.getClaimant() != null) {
                if (job.getClaimant().equals(user.getUuid())) {
                    user.addJob(job);
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

    public Commands getCommands() {
        return commands;
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = new User(player.getUniqueId());
        loadPlayerJobs(user);
        users.add(user);
        if (player.hasPermission(CommandPermissions.LIST_DONE_JOBS)) {
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.DONE)).toList();
            if (!jobs.isEmpty()) {
                sendReviewerJoinMessage(player, jobs.size());
            }
        } else if (player.hasPermission(CommandPermissions.CLAIM_JOB)) {
            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.OPEN)).toList();
            if (!jobs.isEmpty()) {
                sendPlayerJoinMessage(player, jobs.size());
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        getEpicJobsPlayer(event.getPlayer().getUniqueId()).ifPresent(epicJobsPlayer -> users.remove(epicJobsPlayer));
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
