package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.job.Job;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

public class JobEditCommand {

	private final EpicJobs plugin;

	public JobEditCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("Edit a job's description")
	@Permission(CommandPermissions.MODIFY_JOB_DESCRIPTION)
	@Command("job|jobs edit description")
	public void onList(final @NonNull CommandSender sender,
					   final @NonNull @Argument("job") Job job,
					   final @NonNull @Argument("description") String description) {
		job.setDescription(description);
		sender.sendMessage("Set description of job to: " + description);
        plugin.getStorageImplementation().updateJob(job);
	}

	@CommandDescription("Edit a job's location")
	@Permission(CommandPermissions.MODIFY_JOB_LOCATION)
	@Command("job|jobs edit location")
	public void onEditLocation(final @NonNull Player player,
							   final @NonNull @Argument("job") Job job) {
		job.setLocation(player.getLocation());
		player.sendMessage("Set job location to your current on");
		plugin.getStorageImplementation().updateJob(job);
	}

}
