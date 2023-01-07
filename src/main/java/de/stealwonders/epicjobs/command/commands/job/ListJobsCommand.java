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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public record ListJobsCommand(EpicJobs plugin) {

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
			commandSender.sendMessage(Component.text().content("There are no jobs to show.").color(NamedTextColor.RED).build());
			return;
		}

		List<Component> components = new ArrayList<>();
		jobs.forEach(job -> {
			Component component = MiniMessage.miniMessage().deserialize("<aqua><hover:show_text:\"Click to teleport!\"><click:run_command:/job teleport<job>><job></click></hover></aqua>");
			if (verbose) {
				component = component.append(Component.space());
				component = component.append(MiniMessage.miniMessage().deserialize("<gold>(<jobstatus>)</gold>", Placeholder.unparsed("jobstatus", job.getJobStatus().name())));
			}
			components.add(component);
		});

		Component message = Component.join(JoinConfiguration.separator(Component.text(", ").color(Colors.HONEY_YELLOW)), components);

		commandSender.sendMessage("");
		commandSender.sendMessage(message);
		commandSender.sendMessage("");
	}

}