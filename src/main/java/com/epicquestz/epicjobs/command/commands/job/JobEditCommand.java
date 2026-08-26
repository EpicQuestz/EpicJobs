package com.epicquestz.epicjobs.command.commands.job;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobCategory;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

import static com.epicquestz.epicjobs.constants.Messages.JOB_CATEGORY_SET;
import static com.epicquestz.epicjobs.constants.Messages.JOB_CLAIMANT_SET;
import static com.epicquestz.epicjobs.constants.Messages.JOB_DESCRIPTION_SET;
import static com.epicquestz.epicjobs.constants.Messages.JOB_LOCATION_SET;
import static com.epicquestz.epicjobs.constants.Messages.JOB_PROJECT_SET;
import static com.epicquestz.epicjobs.constants.Messages.JOB_STATUS_SET;
import static com.epicquestz.epicjobs.constants.Messages.NOT_PROJECT_MANAGER;

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
		if (!Utils.canManage(sender, job.getProject())) {
			NOT_PROJECT_MANAGER.send(sender);
			return;
		}
		job.setClaimant(claimant.getUniqueId());
		JOB_CLAIMANT_SET.send(sender, Utils.getPlayerHolderText(claimant.getUniqueId()));
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}


	@CommandDescription("Edit a job's description")
	@Permission(CommandPermissions.MODIFY_JOB_DESCRIPTION)
	@Command("description <job> <description>")
	public void onEditDescription(final @NonNull CommandSender sender,
					   @Argument(value = "job", description = "Job") final @NonNull Job job,
					   @Argument(value = "description", description = "Description") final @NonNull @Greedy String description
	) {
		if (!Utils.canManage(sender, job.getProject())) {
			NOT_PROJECT_MANAGER.send(sender);
			return;
		}
		job.setDescription(description);
		JOB_DESCRIPTION_SET.send(sender, description);
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's project")
	@Permission(CommandPermissions.MODIFY_JOB_PROJECT)
	@Command("project <job> <project>")
	public void onEditProject(final @NonNull CommandSender sender,
							  @Argument(value = "job", description = "Job") final @NonNull Job job,
							  @Argument(value = "project", description = "Project", suggestions = "own-project") final @NonNull Project project
	) {
		if (!Utils.canManage(sender, job.getProject()) || !Utils.canManage(sender, project)) {
			NOT_PROJECT_MANAGER.send(sender);
			return;
		}
		job.setProject(project);
		JOB_PROJECT_SET.send(sender, project.getName());
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's location")
	@Permission(CommandPermissions.MODIFY_JOB_LOCATION)
	@Command("location <job>")
	public void onEditLocation(final @NonNull Player player,
							   @Argument(value = "job", description = "Job") final @NonNull Job job) {
		if (!Utils.canManage(player, job.getProject())) {
			NOT_PROJECT_MANAGER.send(player);
			return;
		}
		job.setLocation(player.getLocation());
		JOB_LOCATION_SET.send(player);
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's status")
	@Permission(CommandPermissions.MODIFY_JOB_STATUS)
	@Command("status <job> <status>")
	public void onEditStatus(final @NonNull CommandSender sender,
							 @Argument(value = "job", description = "Job") final @NonNull Job job,
							 @Argument(value = "status", description = "Status") final @NonNull JobStatus status
	) {
		if (!Utils.canManage(sender, job.getProject())) {
			NOT_PROJECT_MANAGER.send(sender);
			return;
		}
		job.setJobStatus(status);
		JOB_STATUS_SET.send(sender, status.name());
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

	@CommandDescription("Edit a job's category")
	@Permission(CommandPermissions.MODIFY_JOB_CATEGORY)
	@Command("category <job> <category>")
	public void onEditCategory(final @NonNull CommandSender sender,
							   @Argument(value = "job", description = "Job") final @NonNull Job job,
							   @Argument(value = "category", description = "Category") final @NonNull JobCategory category
	) {
		if (!Utils.canManage(sender, job.getProject())) {
			NOT_PROJECT_MANAGER.send(sender);
			return;
		}
		job.setJobCategory(category);
		JOB_CATEGORY_SET.send(sender, category);
		EpicJobs.newSharedChain("EpicJobs").async(() -> plugin.getStorage().updateJob(job)).execute();
	}

}
