package de.stealwonders.epicjobs;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.model.job.JobManager;
import de.stealwonders.epicjobs.model.project.ProjectManager;
import de.stealwonders.epicjobs.storage.SettingsFile;
import de.stealwonders.epicjobs.storage.implementation.SqlStorage;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.user.User;
import me.lucko.helper.internal.HelperImplementationPlugin;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@HelperImplementationPlugin
public final class EpicJobs extends ExtendedJavaPlugin implements Listener {

//    private static PlayerUUIDCacheAPI playerUuidCache;

    private static TaskChainFactory taskChainFactory;

    public static <T> TaskChain<T> newSharedChain(final String name) {
        return taskChainFactory.newSharedChain(name);
    }

    private SettingsFile settingsFile;

    private ProjectManager projectManager;
    private JobManager jobManager;

    private HikariDataSource hikariDataSource;
    private StorageImplementation storage;

//    private Commands commands;
    private Set<User> users;

    @Override
    protected void enable() {
        // Plugin startup logic

//        playerUuidCache = getServer().getServicesManager().load(PlayerUUIDCacheAPI.class);

        taskChainFactory = BukkitTaskChainFactory.create(this);

        settingsFile = new SettingsFile(this);

        projectManager = new ProjectManager(this);
        jobManager = new JobManager(this);

        hikariDataSource = new HikariDataSource();
        settingsFile.setupHikari(hikariDataSource, settingsFile.getConfiguration());
        storage = new SqlStorage(this);
        storage.init();
        projectManager.firstLoad();
//        jobManager.firstLoad();

//        commands = new Commands(this);
        users = new HashSet<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            final User user = new User(player.getUniqueId(), player.getName());
            loadPlayerJobs(user);
            users.add(user);
        });

        registerListeners();
    }

    @Override
    protected void disable() {
        // Plugin shutdown logic

        storage.shutdown();

        Bukkit.getOnlinePlayers().forEach(player -> getEpicJobsPlayer(player.getUniqueId()).ifPresent(epicJobsPlayer -> users.remove(epicJobsPlayer)));
    }

//    private void registerListeners() {
//        Bukkit.getPluginManager().registerEvents(this, this);
//    }
//
//    public static @Nullable PlayerUUIDCacheAPI getPlayerUuidCache() {
//        return playerUuidCache;
//    }
//
//    public HikariDataSource getHikariDataSource() {
//        return hikariDataSource;
//    }
//
//    public StorageImplementation getStorage() {
//        return storage;
//    }
//
//    public SettingsFile getSettingsFile() {
//        return settingsFile;
//    }
//
//    public Optional<User> getEpicJobsPlayer(final UUID uuid) {
//        for (final User user : users) {
//            if (user.getUniqueId().equals(uuid)) {
//                return Optional.of(user);
//            }
//        }
//        return Optional.empty();
//    }
//
//    private void loadPlayerJobs(final User user) {
//        for (final Job job : jobManager.getJobs()) {
//            if (job.getClaimant() != null) {
//                if (job.getClaimant().equals(user.getUniqueId())) {
//                    user.addJob(job);
//                }
//            }
//        }
//    }
//
//    public ProjectManager getProjectManager() {
//        return projectManager;
//    }
//
//    public JobManager getJobManager() {
//        return jobManager;
//    }
//
//    @EventHandler
//    public void onJoin(final PlayerJoinEvent event) {
//        final Player player = event.getPlayer();
//        final User user = new User(player.getUniqueId(), player.getName());
//        loadPlayerJobs(user);
//        users.add(user);
//        if (player.hasPermission("epicjobs.command.job.list.done")) {
//            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.DONE)).collect(Collectors.toList());
//            if (jobs.size() >= 1) {
//                sendReviewerJoinMessage(player, jobs.size());
//            }
//        } else if (player.hasPermission("epicjobs.command.job.claim")) {
//            final List<Job> jobs = getJobManager().getJobs().stream().filter(job -> job.getJobStatus().equals(JobStatus.OPEN)).collect(Collectors.toList());
//            if (jobs.size() >= 1) {
//                sendPlayerJoinMessage(player, jobs.size());
//            }
//        }
//    }
//
//    @EventHandler
//    public void onQuit(final PlayerQuitEvent event) {
//        getEpicJobsPlayer(event.getPlayer().getUniqueId()).ifPresent(epicJobsPlayer -> users.remove(epicJobsPlayer));
//    }
//
//    private void sendReviewerJoinMessage(final Player player, final int jobCount) {
//        final TextComponent textComponent = Component.text()
//            .content("There are ").color(NamedTextColor.YELLOW)
//            .append(Component.text(jobCount).color(NamedTextColor.GOLD))
//            .append(Component.text(" job(s) marked as done. Use ").color(NamedTextColor.YELLOW))
//            .append(Component.text("/jobs list done").color(NamedTextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("Review jobs!"))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/jobs list done")))
//            .append(Component.text(" to review them.").color(NamedTextColor.YELLOW))
//            .build();
//        player.sendMessage(textComponent);
////        TextAdapter.sendComponent(player, textComponent);
//    }
//
//    private void sendPlayerJoinMessage(final Player player, final int jobCount) {
//        final TextComponent textComponent = Component.text()
//            .content("There are ").color(NamedTextColor.YELLOW)
//            .append(Component.text(jobCount).color(NamedTextColor.GOLD))
//            .append(Component.text(" job(s) available to be claimed. Use ").color(NamedTextColor.YELLOW))
//            .append(Component.text("/jobs list").color(NamedTextColor.YELLOW).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text("View jobs!"))).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/jobs list")))
//            .append(Component.text(" to find one for yourself.").color(NamedTextColor.YELLOW))
//            .build();
//        player.sendMessage(textComponent);
////        TextAdapter.sendComponent(player, textComponent);
//    }

}
