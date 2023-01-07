package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import de.stealwonders.epicjobs.model.job.JobStatus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public record ListJobsCommand(EpicJobs plugin) {

	private static final Component NO_JOBS = Component.text("There are no jobs to show.").color(NamedTextColor.RED);

	@CommandDescription("List all available jobs")
	@CommandMethod("job|jobs list")
	public void onList(final @NonNull CommandSender commandSender) {
		final List<Job> jobs = plugin.getStorage().getJobs().stream()
				.filter(job -> job.getJobStatus() == JobStatus.OPEN).toList();
		printJobs(commandSender, jobs, false);
	}

	@CommandDescription("List all jobs regardless of its status")
	@CommandPermission(CommandPermissions.LIST_ALL_JOBS)
	@CommandMethod("job|jobs list all")
	public void onListAll(final @NonNull CommandSender commandSender) {
		final List<Job> jobs = plugin.getStorage().getJobs();
		printJobs(commandSender, jobs, true);
	}

	private void printJobs(CommandSender commandSender, List<Job> jobs, boolean verbose) {
		if (jobs.size() < 1) {
			commandSender.sendMessage(NO_JOBS);
			return;
		}

		final List<Component> components = new ArrayList<>();
		jobs.forEach(job -> {
			Component component = Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN)
					.hoverEvent(Component.text("Click to teleport!").color(Colors.CULTURED_WHITE))
					.clickEvent(ClickEvent.runCommand("/job teleport " + job.getId()));
			if (verbose) {
				component = component.append(Component.space());
				component = component.append(Component.text("(" + job.getJobStatus().name() + ")").color(Colors.HONEY_YELLOW));
			}
			components.add(component);
		});

		final Component message = Component.join(JoinConfiguration.separator(Component.text(", ").color(Colors.HONEY_YELLOW)), components);

		commandSender.sendMessage("");
		commandSender.sendMessage(message);
		commandSender.sendMessage("");
	}

}