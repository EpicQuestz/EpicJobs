package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@Command("job|jobs edit")
public class JobEditCommand {

	private final EpicJobs plugin;

	public JobEditCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("Edit a job's claimant")
	@Permission(CommandPermissions.MODIFY_JOB_CLAIMANT)
	@Command("claimant <job> <claimant>")
	public void onEditClaimant(final @NonNull CommandSender sender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "claimant", description = "Claimant", suggestions = "all-offline-players") final @NonNull OfflinePlayer claimant
	) {
		job.setClaimant(claimant.getUniqueId());
		sender.sendMessage(Component.text("Set claimant of job to: " + Utils.getPlayerHolderText(claimant.getUniqueId())));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}


	@CommandDescription("Edit a job's description")
	@Permission(CommandPermissions.MODIFY_JOB_DESCRIPTION)
	@Command("description <job> <description>")
	public void onEditDescription(final @NonNull CommandSender sender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job,
					   @Argument(value = "description", description = "Description") final @NonNull @Greedy String description
	) {
		job.setDescription(description);
		sender.sendMessage(Component.text("Set description of job to: " + description));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's project")
	@Permission(CommandPermissions.MODIFY_JOB_PROJECT)
	@Command("project <job> <project>")
	public void onEditProject(final @NonNull CommandSender sender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "project", description = "Project") final @NonNull Project project
	) {
		job.setProject(project);
		sender.sendMessage(Component.text("Set project of job to: " + project.getName()));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's location")
	@Permission(CommandPermissions.MODIFY_JOB_LOCATION)
	@Command("location <job>")
	public void onEditLocation(final @NonNull Player player,
							   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		job.setLocation(player.getLocation());
		player.sendMessage(Component.text("Set job location to your current position"));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's status")
	@Permission(CommandPermissions.MODIFY_JOB_STATUS)
	@Command("status <job> <status>")
	public void onEditStatus(final @NonNull CommandSender sender,
							 @Argument(value = "job", description = "Job") final @NonNull Job job,
							 @Argument(value = "status", description = "Status") final @NonNull JobStatus status
	) {
		job.setJobStatus(status);
		sender.sendMessage(Component.text("Set job status to: " + status.name()));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's category")
	@Permission(CommandPermissions.MODIFY_JOB_CATEGORY)
	@Command("category <job> <category>")
	public void onEditCategory(final @NonNull CommandSender sender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "category", description = "Category") final @NonNull JobCategory category
	) {
		job.setJobCategory(category);
		sender.sendMessage(Component.text("Set job category to: " + category));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

}
