package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobCategory;
import de.stealwonders.epicjobs.job.JobStatus;
import de.stealwonders.epicjobs.project.Project;
import de.stealwonders.epicjobs.user.EpicJobsPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    @Subcommand("claim|c")
    @CommandCompletion("@open-job")
    public void onClaim(final Player player, final Job job) {
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        if (job == null) {
            JOB_DOESNT_EXIST.send(player);
        } else {
            if (job.getJobStatus() == JobStatus.OPEN) {
                job.claim(epicJobsPlayer);
                ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), job.getId());
            } else {
                JOB_NOT_OPEN.send(player);
            }
        }
    }

    @Subcommand("abandon|a")
    @CommandCompletion("@player-job")
    public void onAbandon(final Player player, @Optional final Job job) {
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        final List<Job> jobs = epicJobsPlayer.getJobs();
        if (job == null) {
            if (jobs.size() == 1) {
                jobs.get(0).abandon(epicJobsPlayer);
                ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), jobs.get(0).getId());
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            if (jobs.contains(job)) {
                job.abandon(epicJobsPlayer);
                ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), job.getId());
            } else {
                PLAYER_HASNT_CLAIMED_JOB.send(player);
            }
        }
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
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        final List<Job> jobs = epicJobsPlayer.getJobs();
        if (job == null) {
            if (jobs.size() == 1) {
                jobs.get(0).setJobStatus(JobStatus.DONE);
                ANNOUNCE_JOB_DONE.broadcast(player.getName(), jobs.get(0).getId());
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            if (jobs.contains(job)) {
                job.setJobStatus(JobStatus.DONE);
                ANNOUNCE_JOB_DONE.broadcast(player.getName(), job.getId());
            } else {
                PLAYER_HASNT_CLAIMED_JOB.send(player);
            }
        }
    }

    @Subcommand("complete")
    @CommandPermission("epicjobs.command.job.complete")
    @CommandCompletion("@player-job")
    public void onComplete(final Player player, final Job job) {
        if (job.getJobStatus() == JobStatus.DONE) {
            job.setJobStatus(JobStatus.COMPLETE);
            JOB_COMPLETED.send(player, job.getId());
        } else {
            JOB_NOT_OPEN.send(player);
        }
    }

    @Subcommand("reopen")
    @CommandPermission("epicjobs.command.job.reopen")
    public void onReopen(final Player player, final Job job) {
        if (job.getJobStatus() == JobStatus.DONE) {
            job.setJobStatus(JobStatus.REOPENED);
        } else if (job.getJobStatus() == JobStatus.COMPLETE) {
            EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
            job.setJobStatus(JobStatus.REOPENED);
            job.setClaimant(null);
            if (epicJobsPlayer != null) {
                epicJobsPlayer.removeJob(job);
            }
            ANNOUNCE_JOB_REOPEN.send(player, job.getId());
        } else {
            JOB_NOT_DONE.send(player);
        }
    }

    @Subcommand("unassign")
    @CommandPermission("epicjobs.command.job.unassign")
    public void onUnassign(final Player player, final Job job) {
        if (job.getJobStatus() == JobStatus.TAKEN || job.getJobStatus() == JobStatus.REOPENED) {
            EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
            job.setClaimant(null);
            if (epicJobsPlayer != null) {
                epicJobsPlayer.removeJob(job);
            }
        } else {
            player.sendMessage("You can only unassign uncomplete jobs taken by a player.");
        }
    }

    @Subcommand("assign")
    @CommandPermission("epicjobs.command.job.assign")
    @CommandCompletion("@open-job")
    public void onAssign(final Player player, final Job job, final Player target) {
        if (job.getJobStatus() == JobStatus.OPEN) {
            EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(target.getUniqueId());
            job.claim(epicJobsPlayer);
            player.sendMessage("You have assigned " + target.getName() + "to job #" + job.getId());
            target.sendMessage("You have been assigned job #" + job.getId());
        } else {
            player.sendMessage("You can only assign untaken jobs to a player.");
        }
    }

    @Subcommand("create")
    @CommandCompletion("@project * *")
    @CommandPermission("epicjobs.command.job.create")
    public void onCreate(final Player player, final Project project, final JobCategory jobCategory, final String description) {
        int id = plugin.getJobManager().getFreeId();
        Job job = new Job(id, player, description, jobCategory, project);
        plugin.getJobManager().addJob(job);
        player.sendMessage("Successfully created job with id #" + id);
    }

    @Subcommand("remove")
    @CommandPermission("epicjobs.command.job.remove")
    public void onRemove(final Player player, final Job job) {
        plugin.getJobManager().removeJob(job);
        job.getProject().removeJob(job);
        EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(job.getClaimant());
        if (epicJobsPlayer != null) {
            epicJobsPlayer.removeJob(job);
        }
        player.sendMessage("Successfully deleted job.");
    }

    //todo edit command

    @Subcommand("stats")
    @CommandPermission("epicjobs.command.job.stats")
    public void onStats(final Player player, @Optional final Player target) {
        EpicJobsPlayer epicJobsPlayer = (target != null) ? plugin.getEpicJobsPlayer(target.getUniqueId()) : plugin.getEpicJobsPlayer(player.getUniqueId());
        player.sendMessage("Completed jobs: " + epicJobsPlayer.getCompletedJobs().size());
    }

}
