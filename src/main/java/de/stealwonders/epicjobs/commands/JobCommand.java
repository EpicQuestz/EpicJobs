package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.iani.playerUUIDCache.PlayerUUIDCacheAPI;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import de.stealwonders.epicjobs.utils.Utils;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.adapter.bukkit.TextAdapter;
import net.kyori.text.event.ClickEvent;
import net.kyori.text.event.HoverEvent;
import net.kyori.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.stealwonders.epicjobs.constants.Messages.*;
import static de.stealwonders.epicjobs.job.JobStatus.TAKEN;

@CommandAlias("job|jobs")
public class JobCommand extends BaseCommand {

    private EpicJobs plugin;

    public JobCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    public void onHelp(final CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("list")
    public void onList(final CommandSender commandSender) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
            .limit(20)
            .collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    @Subcommand("mine")
    public void onMine(final Player player) {
        onListMine(player);
    }

    @Subcommand("list mine")
    public void onListMine(final Player player) {
        final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        if (epicJobsPlayer.isPresent()) {
            final Comparator<Job> comparator = Comparator.comparingInt(job -> job.getJobStatus().getWeight());
            comparator.thenComparingInt(Job::getId);
            final List<Job> jobs = epicJobsPlayer.get().getJobs().stream().sorted(comparator).limit(20).collect(Collectors.toList());
            sendJobList(player, jobs);
        }
    }

    @Subcommand("list near")
    public void onListNear(final Player player, @Default("32") @co.aikar.commands.annotation.Optional final int radius) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.OPEN))
            .filter(job -> job.getLocation().distanceSquared(player.getLocation()) < radius * radius)
            .limit(20)
            .collect(Collectors.toList());
        sendJobList(player, jobs);
    }

    @Subcommand("list project")
    @CommandCompletion("@project")
    public void onListProject(final CommandSender commandSender, final Project project, @co.aikar.commands.annotation.Optional final JobStatus jobStatus, @co.aikar.commands.annotation.Optional final JobCategory jobCategory) {
        Stream<Job> jobStream = plugin.getJobManager().getJobs().stream().filter(job -> job.getProject().equals(project));
        if (jobStatus != null)
            jobStream = jobStream.filter(job -> job.getJobStatus().equals(jobStatus));
        if (jobCategory != null)
            jobStream = jobStream.filter(job -> job.getJobCategory().equals(jobCategory));
        final List<Job> jobs = jobStream.limit(20).collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    @Subcommand("list status")
    public void onListProject(final CommandSender commandSender, final JobStatus jobStatus) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(jobStatus))
            .limit(20)
            .collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    @Subcommand("list category")
    public void onListProject(final CommandSender commandSender, final JobCategory jobCategory) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobCategory().equals(jobCategory))
            .limit(20)
            .collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    @Subcommand("list done")
    @CommandPermission("epicjobs.command.job.list.done")
    public void onListDone(final CommandSender commandSender) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus().equals(JobStatus.DONE))
            .limit(20)
            .collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    @Subcommand("list all")
    @CommandPermission("epicjobs.command.job.list.all")
    public void onListAll(final CommandSender commandSender) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream().limit(20).collect(Collectors.toList());
        sendJobList(commandSender, jobs);
    }

    private void sendJobList(final CommandSender commandSender, final List<Job> jobs) {
        if (jobs.size() >= 1) {
            commandSender.sendMessage("");
            jobs.forEach(job -> {
                final Component text = TextComponent.builder()
                    .content("Job ").append(TextComponent.of("#" + job.getId()).color(TextColor.AQUA)
                    .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Show info!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/job info " + job.getId()))).append(" @ ").color(TextColor.GOLD)
                    .append(TextComponent.builder(
                        String.format("[%s x:%s y:%s z:%s]\n",
                            job.getLocation().getWorld().getName(),
                            job.getLocation().getBlockX(),
                            job.getLocation().getBlockY(),
                            job.getLocation().getBlockZ()
                        )).color(TextColor.AQUA).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to teleport!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/job teleport " + job.getId())))
                    .append(TextComponent.of("Project: ").color(TextColor.GOLD)).append(TextComponent.of(job.getProject().getName()).color(TextColor.YELLOW)).append(TextComponent.of(" Category: ").color(TextColor.GOLD)).append(TextComponent.of(job.getJobCategory().toString() + "\n").color(TextColor.YELLOW))
                    .append(TextComponent.of("Description: ").color(TextColor.GOLD)).append(TextComponent.of(Utils.shortenDescription(job)).color(TextColor.YELLOW))
                    .build();
                TextAdapter.sendComponent(commandSender, text);
                commandSender.sendMessage("");
            });
        } else {
            NO_JOBS_AVAILABLE.send(commandSender);
        }
    }

    @Subcommand("log")
    public void onLog(final Player player) {
        final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        if (epicJobsPlayer.isPresent()) {
            final List<Job> jobs = epicJobsPlayer.get().getJobs();
            jobs.forEach(job -> {
                if (job.getJobStatus().equals(TAKEN)) {
                    jobs.remove(job);
                }
            });
            if (jobs.size() >= 1) {
                player.sendMessage("");
                jobs.sort(Comparator.comparingInt(Job::getId).reversed());
                jobs.stream().limit(20).forEach(job -> {
                    final Component text = TextComponent.builder()
                        .content("Job ").append(TextComponent.of("#" + job.getId()).color(TextColor.AQUA)
                        .hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Show info!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/job info " + job.getId()))).append(" @ ").color(TextColor.GOLD)
                        .append(TextComponent.builder(
                            String.format("[%s x:%s y:%s z:%s]\n",
                                job.getLocation().getWorld().getName(),
                                job.getLocation().getBlockX(),
                                job.getLocation().getBlockY(),
                                job.getLocation().getBlockZ()
                            )).color(TextColor.AQUA).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to teleport!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/job teleport " + job.getId())))
                        .append(TextComponent.of("Project: ").color(TextColor.GOLD)).append(TextComponent.of(job.getProject().getName()).color(TextColor.YELLOW))
                        .append(TextComponent.of(" Category: ").color(TextColor.GOLD)).append(TextComponent.of(job.getJobCategory().toString()).color(TextColor.YELLOW))
                        .append(TextComponent.of(" Status: ").color(TextColor.GOLD)).append(TextComponent.of(job.getJobStatus().toString() + "\n").color(TextColor.YELLOW))
                        .append(TextComponent.of("Description: ").color(TextColor.GOLD)).append(TextComponent.of(Utils.shortenDescription(job)).color(TextColor.YELLOW))
                        .build();
                    TextAdapter.sendComponent(player, text);
                    player.sendMessage("");
                });
            } else {
                NO_JOBS_AVAILABLE.send(player);
            }
        }
    }

    @Subcommand("info")
    public void onInfo(final CommandSender sender, final Job job) {
        final Component text = TextComponent.builder()
            .content("Job #" + job.getId() + " @ ").color(TextColor.GOLD)
            .append(TextComponent.builder(
                String.format("[%s x:%s y:%s z:%s]\n",
                    job.getLocation().getWorld().getName(),
                    job.getLocation().getBlockX(),
                    job.getLocation().getBlockY(),
                    job.getLocation().getBlockZ()
                )).color(TextColor.AQUA).hoverEvent(HoverEvent.of(HoverEvent.Action.SHOW_TEXT, TextComponent.of("Click to teleport!"))).clickEvent(ClickEvent.of(ClickEvent.Action.RUN_COMMAND, "/job teleport " + job.getId())))
            .append(TextComponent.of("Project: ").color(TextColor.GOLD)).append(TextComponent.of(job.getProject().getName()).color(TextColor.YELLOW))
            .append(TextComponent.of(" Category: ").color(TextColor.GOLD)).append(TextComponent.of(job.getJobCategory().toString()).color(TextColor.YELLOW))
            .append(TextComponent.of(" Status: ").color(TextColor.GOLD)).append(TextComponent.of(job.getJobStatus().toString() + "\n").color(TextColor.YELLOW))
            .append(TextComponent.of("Leader: ").color(TextColor.GOLD)).append(TextComponent.of(Utils.getPlayerHolderText(job.getCreator())).color(TextColor.YELLOW))
            .append(TextComponent.of(" Claimant: ").color(TextColor.GOLD)).append(TextComponent.of(Utils.getPlayerHolderText(job.getClaimant()) + "\n").color(TextColor.YELLOW))
            .append(TextComponent.of("Description: ").color(TextColor.GOLD)).append(TextComponent.of(job.getDescription()).color(TextColor.YELLOW))
            .build();
        sender.sendMessage("");
        TextAdapter.sendComponent(sender, text);
        sender.sendMessage("");
    }

    @Subcommand("claim|c")
    @CommandCompletion("@open-job")
    public void onClaim(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                if (job == null) {
                    JOB_DOESNT_EXIST.send(player);
                    return false;
                } else {
                    if (job.getJobStatus().equals(JobStatus.OPEN)) {
                        if (epicJobsPlayer.isPresent()) {
                            job.claim(epicJobsPlayer.get());
                            ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), job.getId());
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        JOB_NOT_OPEN.send(player);
                        return false;
                    }
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
    }

    @Subcommand("abandon|a")
    @CommandCompletion("@player-job")
    public void onAbandon(final Player player, @co.aikar.commands.annotation.Optional final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                final List<Job> jobs = epicJobsPlayer.isPresent() ? epicJobsPlayer.get().getJobs() : new ArrayList<>();
                Job jobEdited = null;
                if (epicJobsPlayer.isPresent()) {
                    if (job == null) {
                        if (jobs.size() == 1) {
                            if (jobs.get(0).getJobStatus().equals(TAKEN) || jobs.get(0).getJobStatus().equals(JobStatus.DONE)) {
                                jobEdited = jobs.get(0);
                                jobEdited.abandon(epicJobsPlayer.get());
                                ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), jobs.get(0).getId());
                            } else {
                                JOB_CANT_BE_ABANDONED.send(player);
                            }
                        } else if (jobs.size() == 0) {
                            PLAYER_HAS_NO_JOBS.send(player);
                        } else {
                            PLAYER_HAS_MULITPLE_JOBS.send(player);
                        }
                    } else {
                        if (jobs.contains(job)) {
                            if (jobs.get(0).getJobStatus().equals(TAKEN) || jobs.get(0).getJobStatus().equals(JobStatus.DONE)) {
                                job.abandon(epicJobsPlayer.get());
                                ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), job.getId());
                                jobEdited = job;
                            } else {
                                JOB_CANT_BE_ABANDONED.send(player);
                            }
                        } else {
                            PLAYER_HASNT_CLAIMED_JOB.send(player);
                        }
                    }
                }
                return jobEdited;
            })
            .abortIfNull()
            .asyncLast((jobEdited) -> plugin.getStorageImplementation().updateJob(jobEdited))
            .execute();
    }

    @Subcommand("teleport|tp")
    @CommandCompletion("@player-job")
    public void onTeleport(final Player player, @co.aikar.commands.annotation.Optional final Job job) {
        if (job == null) {
            final List<Job> jobs =  plugin.getEpicJobsPlayer(player.getUniqueId()).get().getJobs();
            if (jobs.size() == 1) {
                jobs.get(0).teleport(player);
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            job.teleport(player);
        }
    }

    @Subcommand("done")
    @CommandCompletion("@player-job")
    public void onDone(final Player player, @co.aikar.commands.annotation.Optional final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId()).get();
                final List<Job> jobs = epicJobsPlayer.getActiveJobs();
                Job jobEdited = null;

                if (job == null) {
                    if (jobs.size() == 1) {
                            jobEdited = jobs.get(0);
                        if (jobEdited.getJobStatus().equals(TAKEN)) {
                            jobEdited.setJobStatus(JobStatus.DONE);
                            ANNOUNCE_JOB_DONE.broadcast(player.getName(), jobs.get(0).getId());
                        } else {
                            JOB_HAS_TO_BE_ACTIVE.send(player);
                        }
                    } else if (jobs.size() == 0) {
                        PLAYER_HAS_NO_ACTIVE_JOBS.send(player);
                    } else {
                        PLAYER_HAS_MULITPLE_JOBS.send(player);
                    }
                } else {
                    if (jobs.contains(job)) {
                        if (job.getJobStatus().equals(TAKEN)) {
                            job.setJobStatus(JobStatus.DONE);
                            ANNOUNCE_JOB_DONE.broadcast(player.getName(), job.getId());
                            jobEdited = job;
                        } else {
                            JOB_HAS_TO_BE_ACTIVE.send(player);
                        }
                    } else {
                        PLAYER_HASNT_CLAIMED_JOB.send(player);
                    }
                }
                return jobEdited;
            })
            .abortIfNull()
            .asyncLast((jobedited) -> plugin.getStorageImplementation().updateJob(jobedited))
            .execute();
    }

    @Subcommand("complete")
    @CommandPermission("epicjobs.command.job.complete")
    @CommandCompletion("@player-job")
    public void onComplete(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.DONE)) {
                    job.setJobStatus(JobStatus.COMPLETE);
                    JOB_COMPLETED.send(player, job.getId());
                    return true;
                } else {
                    JOB_CANT_BE_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
    }

    @Subcommand("reopen")
    @CommandPermission("epicjobs.command.job.reopen")
    public void onReopen(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                switch (job.getJobStatus()) {
                    case COMPLETE:
                    case TAKEN:
                        job.setJobStatus(JobStatus.OPEN);
                        job.setClaimant(null);
                        final Optional<EpicJobsPlayer> epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
                        epicJobsPlayer.ifPresent(jobsPlayer -> jobsPlayer.removeJob(job));
                        ANNOUNCE_JOB_REOPEN.send(player, player.getName(), job.getId());
                        return true;
                    case DONE:
                        job.setJobStatus(JobStatus.TAKEN);
                        final PlayerUUIDCacheAPI playerUUIDCacheAPI = EpicJobs.getPlayerUuidCache();
                        if (playerUUIDCacheAPI != null) {
                            final String username = job.getClaimant() != null ? playerUUIDCacheAPI.getPlayerFromNameOrUUID(job.getClaimant().toString()).getName() : "<none>";
                            JOB_REOPEN.send(player, job.getId(), username);
                            return true;
                        }
                    default:
                        JOB_NOT_DONE.send(player);
                        return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
    }

    @Subcommand("unassign")
    @CommandPermission("epicjobs.command.job.unassign")
    public void onUnassign(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(TAKEN)) {
                    final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant()).get();
                    job.setClaimant(null);
                    if (epicJobsPlayer != null) {
                        epicJobsPlayer.removeJob(job);
                    }
                    return true;
                } else {
                    JOB_CANT_BE_UNASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
    }

    @Subcommand("assign")
    @CommandPermission("epicjobs.command.job.assign")
    @CommandCompletion("@open-job")
    public void onAssign(final Player player, final Job job, final Player target) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus().equals(JobStatus.OPEN)) {
                    final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(target.getUniqueId()).get();
                    job.claim(epicJobsPlayer);
                    HAS_ASSIGNED_JOB.send(player, target.getName(), job.getId());
                    HAS_BEEN_ASSIGNED_JOB.send(target, job.getId());
                    return true;
                } else {
                    JOB_CANT_BE_ASSIGNED.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorageImplementation().updateJob(job))
            .execute();
    }

    @Subcommand("create")
    @CommandCompletion("@project * *")
    @CommandPermission("epicjobs.command.job.create")
    public void onCreate(final Player player, final Project project, final JobCategory jobCategory, final String description) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.ACTIVE)) {
                    CREATING_JOB.sendActionbar(player);
                    return true;
                } else {
                    PROJECT_ALREADY_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> {
                final Job job = plugin.getStorageImplementation().createAndLoadJob(player.getUniqueId(), description, project, player.getLocation(), JobStatus.OPEN, jobCategory);
                plugin.getJobManager().addJob(job);
                return job;
            })
            .syncLast((job) -> {
                final String message = (job == null) ? "§cError while creating job. Please contact an administrator." : SUCCESSFULLY_CREATED_JOB.toString(job.getId());
                player.sendMessage(message);
            })
            .execute();
    }

    @Subcommand("remove")
    @CommandPermission("epicjobs.command.job.remove")
    public void onRemove(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .sync(() -> {
                plugin.getJobManager().removeJob(job);
                job.getProject().removeJob(job);
                final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant()).get();
                if (epicJobsPlayer != null) {
                    epicJobsPlayer.removeJob(job);
                }
                REMOVING_JOB.sendActionbar(player, job.getId());
            })
            .async(() -> plugin.getStorageImplementation().deleteJob(job))
            .syncLast((i) -> SUCCESSFULLY_REMOVED_JOB.send(player)
        ).execute();
    }

    //todo edit command

    @Subcommand("stats")
    @CommandPermission("epicjobs.command.job.stats")
    public void onStats(final Player player, @co.aikar.commands.annotation.Optional final Player target) {
        final EpicJobsPlayer epicJobsPlayer = (target != null) ? plugin.getEpicJobsPlayer(target.getUniqueId()).get() : plugin.getEpicJobsPlayer(player.getUniqueId()).get();
        player.sendMessage("Completed jobs: " + epicJobsPlayer.getCompletedJobs().size());
    }

}
