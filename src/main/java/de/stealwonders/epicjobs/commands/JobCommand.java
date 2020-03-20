package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.project.ProjectStatus;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.stealwonders.epicjobs.constants.Messages.*;

@CommandAlias("job|jobs")
public class JobCommand extends BaseCommand {

    private EpicJobs plugin;

    public JobCommand(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    @Default
    @HelpCommand
    public void onHelp(final CommandSender commandSender, final CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("list")
    public void onList(final CommandSender sender) {
        final List<Job> jobs = plugin.getJobManager().getJobs().stream()
            .filter(job -> job.getJobStatus() == JobStatus.OPEN)
            .collect(Collectors.toList());
        if (jobs.size() >= 1) {
            jobs.forEach(job -> sender.sendMessage("#" + job.getId() + " | " + job.getDescription()));
        } else {
            NO_JOBS_AVAILABLE.send(sender);
        }
    }

    @Subcommand("list mine")
    public void onListMine(final Player player) {
        final List<Job> jobs = new ArrayList(plugin.getEpicJobsPlayer(player.getUniqueId()).getJobs());
        Comparator<Job> comparator = Comparator.comparingInt(job -> job.getJobStatus().getWeight());
        comparator.thenComparingInt(Job::getId);
        jobs.sort(comparator);
        if (jobs.size() >= 1) {
            jobs.forEach(job -> player.sendMessage("#" + job.getId() + " | " + job.getDescription()));
        } else {
            NO_JOBS_AVAILABLE.send(player);
        }
    }

    @Subcommand("list all")
    @CommandPermission("epicjobs.command.job.listall")
    public void onListAll(final CommandSender sender) {
        final List<Job> jobs = plugin.getJobManager().getJobs();
        if (jobs.size() >= 1) {
            jobs.forEach(job -> sender.sendMessage("#" + job.getId() + " | " +  job.getProject().getName() + ": " + job.getJobCategory() + " '" + job.getDescription() + "' [" + job.getClaimant() + " " +  job.getJobStatus() + "]"));
        } else {
            NO_JOBS_AVAILABLE.send(sender);
        }
    }

    @Subcommand("claim|c")
    @CommandCompletion("@open-job")
    public void onClaim(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                if (job == null) {
                    JOB_DOESNT_EXIST.send(player);
                    return false;
                } else {
                    if (job.getJobStatus() == JobStatus.OPEN) {
                        job.claim(epicJobsPlayer);
                        ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), job.getId());
                        return true;
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
    public void onAbandon(final Player player, @Optional final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                final List<Job> jobs = epicJobsPlayer.getJobs();
                Job jobEdited = null;
                if (job == null) {
                    if (jobs.size() == 1) {
                        if (jobs.get(0).getJobStatus() == JobStatus.TAKEN || jobs.get(0).getJobStatus() == JobStatus.DONE) {
                            jobEdited = jobs.get(0);
                            jobEdited.abandon(epicJobsPlayer);
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
                        if (jobs.get(0).getJobStatus() == JobStatus.TAKEN || jobs.get(0).getJobStatus() == JobStatus.DONE) {
                            job.abandon(epicJobsPlayer);
                            ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), job.getId());
                            jobEdited = job;
                        } else {
                            JOB_CANT_BE_ABANDONED.send(player);
                        }
                    } else {
                        PLAYER_HASNT_CLAIMED_JOB.send(player);
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
    public void onTeleport(final Player player, @Optional final Job job) {
        if (job == null) {
            final List<Job> jobs =  plugin.getEpicJobsPlayer(player.getUniqueId()).getJobs();
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
    public void onDone(final Player player, @Optional final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
                final List<Job> jobs = epicJobsPlayer.getActiveJobs();
                Job jobEdited = null;

                if (job == null) {
                    if (jobs.size() == 1) {
                            jobEdited = jobs.get(0);
                        if (jobEdited.getJobStatus() == JobStatus.TAKEN) {
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
                        if (job.getJobStatus() == JobStatus.TAKEN) {
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
                if (job.getJobStatus() == JobStatus.DONE) {
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

    //todo !!!!!!!!!!!

    @Subcommand("reopen")
    @CommandPermission("epicjobs.command.job.reopen")
    public void onReopen(final Player player, final Job job) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (job.getJobStatus() == JobStatus.DONE) {
                    job.setJobStatus(JobStatus.OPEN);
                    return true;
                } else if (job.getJobStatus() == JobStatus.COMPLETE) {
                    EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
                    job.setJobStatus(JobStatus.OPEN);
                    job.setClaimant(null);
                    if (epicJobsPlayer != null) {
                        epicJobsPlayer.removeJob(job);
                    }
                    ANNOUNCE_JOB_REOPEN.send(player, job.getId());
                    return true;
                } else {
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
                if (job.getJobStatus() == JobStatus.TAKEN) {
                    EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
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
                if (job.getJobStatus() == JobStatus.OPEN) {
                    EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(target.getUniqueId());
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
                if (project.getProjectStatus() == ProjectStatus.ACTIVE) {
                    CREATING_JOB.sendActionbar(player);
                    return true;
                } else {
                    PROJECT_ALREADY_COMPLETE.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> {
                Job job = plugin.getStorageImplementation().createAndLoadJob(player.getUniqueId(), description, project, player.getLocation(), JobStatus.OPEN, jobCategory);
                plugin.getJobManager().addJob(job);
                return job;
            })
            .syncLast((job) -> {
                String message = (job == null) ? "§cError while creating job. Please contact an administrator." : SUCCESSFULLY_CREATED_JOB.toString(job.getId());
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
                EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
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
    public void onStats(final Player player, @Optional final Player target) {
        EpicJobsPlayer epicJobsPlayer = (target != null) ? plugin.getEpicJobsPlayer(target.getUniqueId()) : plugin.getEpicJobsPlayer(player.getUniqueId());
        player.sendMessage("Completed jobs: " + epicJobsPlayer.getCompletedJobs().size());
    }

}
