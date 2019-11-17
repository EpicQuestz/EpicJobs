package de.stealwonders.epicjobs.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.job.Job;
import de.stealwonders.epicjobs.job.JobStatus;
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
    @Syntax("<job>")
    public void onClaim(final Player player, final Integer id) {
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        final Job job = plugin.getJobManager().getJobById(id);
        if (job == null) {
            JOB_DOESNT_EXIST.send(player);
        } else {
            if (job.getJobStatus() == JobStatus.OPEN) {
                job.claim(epicJobsPlayer);
                ANNOUNCE_JOB_TAKEN.broadcast(player.getName(), id);
            } else {
                JOB_NOT_OPEN.send(player);
            }
        }
    }

    @Subcommand("abandon|a")
    @Syntax("[job]")
    public void onAbandon(final Player player, @Optional final Integer id) {
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        final List<Job> jobs = epicJobsPlayer.getJobs();
        if (id == null) {
            if (jobs.size() == 1) {
                jobs.get(0).abandon(epicJobsPlayer);
                ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), jobs.get(0).getId());
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            final Job job = plugin.getJobManager().getJobById(id);
            if (job == null) {
                JOB_DOESNT_EXIST.send(player);
            } else {
                if (jobs.contains(job)) {
                    job.abandon(epicJobsPlayer);
                    ANNOUNCE_JOB_ABANDONMENT.broadcast(player.getName(), id);
                } else {
                    PLAYER_HASNT_CLAIMED_JOB.send(player);
                }
            }
        }
    }

    @Subcommand("teleport|tp")
    @Syntax("[job]")
    public void onTeleport(final Player player, @Optional final Integer id) {
        if (id == null) {
            final List<Job> jobs =  plugin.getEpicJobsPlayer(player.getUniqueId()).getJobs();
            if (jobs.size() == 1) {
                jobs.get(0).teleport(player);
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            final Job job = plugin.getJobManager().getJobById(id);
            if (job != null) {
                job.teleport(player);
            } else {
                JOB_DOESNT_EXIST.send(player);
            }
        }
    }

    @Subcommand("done")
    public void onDone(final Player player, @Optional final Integer id) {
        final EpicJobsPlayer epicJobsPlayer = plugin.getEpicJobsPlayer(player.getUniqueId());
        final List<Job> jobs = epicJobsPlayer.getJobs();
        if (id == null) {
            if (jobs.size() == 1) {
                jobs.get(0).setJobStatus(JobStatus.DONE);
                ANNOUNCE_JOB_DONE.broadcast(player.getName(), jobs.get(0).getId());
            } else if (jobs.size() == 0) {
                PLAYER_HAS_NO_JOBS.send(player);
            } else {
                PLAYER_HAS_MULITPLE_JOBS.send(player);
            }
        } else {
            final Job job = plugin.getJobManager().getJobById(id);
            if (job == null) {
                JOB_DOESNT_EXIST.send(player);
            } else {
                if (jobs.contains(job)) {
                    job.setJobStatus(JobStatus.DONE);
                    ANNOUNCE_JOB_DONE.broadcast(player.getName(), id);
                } else {
                    PLAYER_HASNT_CLAIMED_JOB.send(player);
                }
            }
        }
    }

    @Subcommand("complete")
    @CommandPermission("epicjobs.command.complete")
    public void onComplete(final Player player, final Integer id) {
        final Job job = plugin.getJobManager().getJobById(id);
        if (job == null) {
            JOB_DOESNT_EXIST.send(player);
        } else {
            if (job.getJobStatus() == JobStatus.DONE) {
                job.setJobStatus(JobStatus.COMPLETE);
                JOB_COMPLETED.send(player, id);
            } else {
                JOB_NOT_OPEN.send(player);
            }
        }
    }

    @Subcommand("reopen")
    @CommandPermission("epicjobs.command.reopen")
    public void onReopen(final Player player, final Integer id) {
        final Job job = plugin.getJobManager().getJobById(id);
        if (job == null) {
            JOB_DOESNT_EXIST.send(player);
        } else {
            if (job.getJobStatus() == JobStatus.DONE || job.getJobStatus() == JobStatus.COMPLETE) {
                job.setJobStatus(JobStatus.REOPENED);
                ANNOUNCE_JOB_REOPEN.send(player, id);
            } else {
                JOB_NOT_DONE.send(player);
            }
        }
    }

}
