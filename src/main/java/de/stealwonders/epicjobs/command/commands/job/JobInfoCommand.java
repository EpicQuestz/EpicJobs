package de.stealwonders.epicjobs.command.commands.job;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.misc.Colors;
import de.stealwonders.epicjobs.model.job.Job;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record JobInfoCommand(EpicJobs plugin) {

//	private static final Component CREATE_ERROR = Component.text("An error occurred while creating the job.").color(NamedTextColor.RED);

	@CommandDescription("See a job description")
	@CommandMethod("job|jobs info <job>")
	public void onCommand(final @NonNull CommandSender commandSender,
						  @Argument(value = "job", description = "Job") final @NonNull Job job) {
		final Component jobComponent = Component.text("Job").color(Colors.HONEY_YELLOW)
				.append(Component.space())
				.append(Component.text("#" + job.getId()).color(Colors.CARIBBEAN_GREEN));

		final Component projectComponent = Component.text(
						String.format("[%s x:%s y:%s z:%s]",
								job.getLocation().getWorld().getName(),
								job.getLocation().getBlockX(),
								job.getLocation().getBlockY(),
								job.getLocation().getBlockZ()
						))
				.color(Colors.CARIBBEAN_GREEN)
				.hoverEvent(Component.text("Click to teleport!").color(Colors.CULTURED_WHITE))
				.clickEvent(ClickEvent.runCommand("/job teleport " + job.getId()));

		final Component text = Component.text()
				.append(jobComponent)
				.appendSpace()
				.append(Component.text("@").color(Colors.CULTURED_WHITE))
				.appendSpace()
				.append(projectComponent)
				.appendNewline()
				.append(Component.text("Project:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text(job.getProject().getName()).color(Colors.CULTURED_WHITE))
				.appendSpace()
				.append(Component.text("Category:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text(job.getJobCategory().toString()).color(Colors.CULTURED_WHITE))
				.appendSpace()
				.append(Component.text("Status:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text(job.getJobStatus().toString()).color(Colors.CULTURED_WHITE))
				.appendNewline()
				.append(Component.text("Leader:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text("sjoerdtim")).color(Colors.CULTURED_WHITE)
				.appendSpace()
				.append(Component.text("Claimant:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text("sjoerdtim")).color(Colors.CULTURED_WHITE)
				.appendNewline()
				.append(Component.text("Description:").color(Colors.HONEY_YELLOW))
				.appendSpace()
				.append(Component.text(job.getDescription()).color(Colors.CULTURED_WHITE))
				.build();

		commandSender.sendMessage("");
		commandSender.sendMessage(text);
		commandSender.sendMessage("");
	}

}