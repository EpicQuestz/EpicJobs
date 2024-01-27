package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import org.bukkit.OfflinePlayer;
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

	@CommandDescription("Edit a job's claimant")
	@Permission(CommandPermissions.MODIFY_JOB_CLAIMANT)
	@Command("job|jobs edit claimant")
	public void onEditClaimant(final @NonNull CommandSender sender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "claimant", description = "Claimant") final @NonNull OfflinePlayer claimant
	) {
		job.setClaimant(claimant.getUniqueId());
		sender.sendMessage("Set claimant of job to: " + claimant.getName());
		plugin.getStorageImplementation().updateJob(job);
	}


	@CommandDescription("Edit a job's description")
	@Permission(CommandPermissions.MODIFY_JOB_DESCRIPTION)
	@Command("job|jobs edit description")
	public void onList(final @NonNull CommandSender sender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job,
					   @Argument(value = "description", description = "Description") final @NonNull String description
	) {
		job.setDescription(description);
		sender.sendMessage("Set description of job to: " + description);
        plugin.getStorageImplementation().updateJob(job);
	}

	@CommandDescription("Edit a job's project")
	@Permission(CommandPermissions.MODIFY_JOB_PROJECT)
	@Command("job|jobs edit project")
	public void onEditProject(final @NonNull CommandSender sender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "project", description = "Project") final @NonNull Project project
	) {
		job.setProject(project);
		sender.sendMessage("Set project of job to: " + project.getName());
		plugin.getStorageImplementation().updateJob(job);
	}

	@CommandDescription("Edit a job's location")
	@Permission(CommandPermissions.MODIFY_JOB_LOCATION)
	@Command("job|jobs edit location")
	public void onEditLocation(final @NonNull Player player,
							   @Argument(value = "job", description = "Job") final @NonNull Job job
	) {
		job.setLocation(player.getLocation());
		player.sendMessage("Set job location to your current on");
		plugin.getStorageImplementation().updateJob(job);
	}

	@CommandDescription("Edit a job's status")
	@Permission(CommandPermissions.MODIFY_JOB_STATUS)
	@Command("job|jobs edit status")
	public void onEditStatus(final @NonNull CommandSender sender,
							 @Argument(value = "job", description = "Job") final @NonNull Job job,
							 @Argument(value = "status", description = "Status") final @NonNull JobStatus status
	) {
		job.setJobStatus(status);
		sender.sendMessage("Set job status to: " + status.name());
		plugin.getStorageImplementation().updateJob(job);
	}

	@CommandDescription("Edit a job's category")
	@Permission(CommandPermissions.MODIFY_JOB_CATEGORY)
	@Command("job|jobs edit category")
	public void onEditCategory(final @NonNull CommandSender sender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "category", description = "Category") final @NonNull JobCategory category
	) {
		job.setJobCategory(category);
		sender.sendMessage("Set job category to: " + category);
		plugin.getStorageImplementation().updateJob(job);
	}

}
