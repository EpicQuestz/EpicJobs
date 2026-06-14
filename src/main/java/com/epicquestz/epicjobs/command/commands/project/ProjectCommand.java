package com.epicquestz.epicjobs.command.commands.project;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.command.CommandPermissions;
import com.epicquestz.epicjobs.constants.Palette;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.job.JobStatus;
import com.epicquestz.epicjobs.project.Project;
import com.epicquestz.epicjobs.project.ProjectStatus;
import com.epicquestz.epicjobs.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.epicquestz.epicjobs.constants.Messages.ANNOUNCE_PROJECT_COMPLETION;
import static com.epicquestz.epicjobs.constants.Messages.CANT_CREATE_PROJECT;
import static com.epicquestz.epicjobs.constants.Messages.ERROR_CREATING_PROJECT;
import static com.epicquestz.epicjobs.constants.Messages.NO_PROJECTS_AVAILABLE;
import static com.epicquestz.epicjobs.constants.Messages.NO_STATS_AVAILABLE;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_ALREADY_COMPLETE;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_HAS_NO_COMPLETED_JOBS;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_ALREADY_PAUSED;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_NOT_PAUSED;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_PAUSED;
import static com.epicquestz.epicjobs.constants.Messages.PROJECT_RESUMED;
import static com.epicquestz.epicjobs.constants.Messages.SUCCESSFULLY_CREATED_PROJECT;

@Command("project|projects")
public class ProjectCommand {

	private final EpicJobs plugin;

	public ProjectCommand(EpicJobs plugin) {
		this.plugin = plugin;
	}

	@CommandDescription("List projects")
	@Permission(CommandPermissions.LIST_PROJECTS)
	@Command("list|ls")
	public void onList(final @NonNull CommandSender sender) {
		final List<Project> projects = plugin.getProjectManager().getProjects().stream()
            .filter(project -> project.getProjectStatus().equals(ProjectStatus.ACTIVE))
            .toList();
		sendProjectList(sender, projects);
	}

	@CommandDescription("List all projects")
	@Permission(CommandPermissions.LIST_ALL_PROJECTS)
	@Command("list|ls all")
	public void onListAll(final @NonNull CommandSender sender) {
		final List<Project> projects = plugin.getProjectManager().getProjects();
		sendProjectList(sender, projects);
	}

	@CommandDescription("Show project info")
	@Permission(CommandPermissions.INFO_PROJECT)
	@Command("info <project>")
	public void onInfo(final @NonNull CommandSender sender,
					   @Argument(value = "project", description = "Project") final @NonNull Project project) {
		final List<UUID> deputies = project.getDeputies();
		final String deputyNames = deputies.isEmpty()
			? "None"
			: deputies.stream().map(Utils::getPlayerHolderText).collect(Collectors.joining(", "));
		final String created = DateTimeFormatter.ofPattern("yyyy-MM-dd")
			.withZone(ZoneId.systemDefault())
			.format(Instant.ofEpochMilli(project.getCreationTime()));

		final TextComponent text = Component.text()
			.content(project.getName() + " @ ").color(Palette.Role.INFO.accent()).decoration(TextDecoration.BOLD, true)
			.append(Component.text(
				String.format("[%s x:%s y:%s z:%s]\n\n",
					project.getLocation().getWorld().getName(),
					project.getLocation().getBlockX(),
					project.getLocation().getBlockY(),
					project.getLocation().getBlockZ()
				), Palette.Role.INFO.accent()).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.UNDERLINED, true).hoverEvent(HoverEvent.showText(Component.text("Click to teleport!", Palette.MUTED))).clickEvent(ClickEvent.runCommand("/project teleport " + project.getName())))
			.append(Component.text()
				.decoration(TextDecoration.BOLD, false)
				.append(Component.text("Leader: ", Palette.MUTED)).append(Component.text(Utils.getPlayerHolderText(project.getLeader()), Palette.Role.INFO.body()))
				.append(Component.text(" Status: ", Palette.MUTED)).append(Component.text(project.getProjectStatus().toString(), Palette.Role.INFO.body()))
				.append(Component.text(" Created: ", Palette.MUTED)).append(Component.text(created + "\n", Palette.Role.INFO.body()))
				.append(Component.text("Deputies: ", Palette.MUTED)).append(Component.text(deputyNames, Palette.Role.INFO.body())))
			.build();
		sender.sendMessage(Component.empty());
		sender.sendMessage(text);
		sender.sendMessage(Component.empty());
	}

	private static void sendProjectList(@NotNull CommandSender sender, List<Project> projects) {
		if (projects.isEmpty()) {
			NO_PROJECTS_AVAILABLE.send(sender);
			return;
		}

		final List<TextComponent> textComponents = new ArrayList<>();
		projects.forEach(project -> {
			final TextComponent textComponent = Component.text(project.getName(), Palette.Role.INFO.accent())
					.hoverEvent(HoverEvent.showText(Component.text("Click to teleport!", Palette.MUTED)))
					.clickEvent(ClickEvent.runCommand("/project teleport " + project.getName()));
			textComponents.add(textComponent);
		});
		final JoinConfiguration joinConfiguration = JoinConfiguration.builder()
				.separator(Component.text(", ", Palette.MUTED))
				.lastSeparator(Component.text(" and ", Palette.MUTED))
				.build();
		final Component message = Component.join(joinConfiguration, textComponents);
		sender.sendMessage(Component.empty());
		sender.sendMessage(message);
		sender.sendMessage(Component.empty());
	}

	@CommandDescription("Create a project")
	@Permission(CommandPermissions.CREATE_PROJECT)
	@Command("create <name> [leader]")
	public void onCreate(final @NonNull Player player,
						 @Argument(value = "name", description = "Name") final @NonNull String name,
						 @Argument(value = "leader", description = "Leader") final @Nullable Player leader
	) {
		EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (plugin.getProjectManager().getProjectByName(name) == null) {
                    return true;
                } else {
                    CANT_CREATE_PROJECT.send(player);
                    return false;
                }
            })
            .abortIf(false)
            .asyncFirst(() -> {
                final Project project = (leader == null) ? plugin.getStorage().createAndLoadProject(name, player.getUniqueId(), player.getLocation(), ProjectStatus.ACTIVE) : plugin.getStorage().createAndLoadProject(name, leader.getUniqueId(), leader.getLocation(), ProjectStatus.ACTIVE);
                plugin.getProjectManager().addProject(project);
                return project;
            })
            .syncLast((project) -> {
                if (project == null) {
                    ERROR_CREATING_PROJECT.send(player);
                } else {
                    SUCCESSFULLY_CREATED_PROJECT.send(player, project.getId());
                }
            })
            .execute();
	}

	@CommandDescription("Teleport to a project")
	@Permission(CommandPermissions.TELEPORT_PROJECT)
	@Command("teleport|tp <project>")
	public void onTeleport(final @NonNull Player player,
						   @Argument(value = "project", description = "Project") final @NonNull Project project) {
		project.teleport(player);
	}

	@CommandDescription("Pause a project")
	@Permission(CommandPermissions.PAUSE_PROJECT)
	@Command("pause <project>")
	public void onPause(final @NonNull CommandSender sender,
						@Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project) {
		EpicJobs.newSharedChain("EpicJobs")
			.syncFirst(() -> {
				if (!project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
					project.setProjectStatus(ProjectStatus.PAUSED);
					PROJECT_PAUSED.send(sender, project.getName());
					return true;
				} else {
					PROJECT_ALREADY_PAUSED.send(sender);
					return false;
				}
			})
			.abortIf(false)
			.async(() -> plugin.getStorage().updateProject(project))
			.execute();
	}

	@CommandDescription("Resume a project")
	@Permission(CommandPermissions.RESUME_PROJECT)
	@Command("resume|unpause <project>")
	public void onResume(final @NonNull CommandSender sender,
						 @Argument(value = "project", description = "Project", suggestions = "paused-project") final @NonNull Project project) {
        EpicJobs.newSharedChain("EpicJobs")
            .syncFirst(() -> {
                if (project.getProjectStatus().equals(ProjectStatus.PAUSED)) {
                    project.setProjectStatus(ProjectStatus.ACTIVE);
                    PROJECT_RESUMED.send(sender, project.getName());
                    return true;
                } else {
					PROJECT_NOT_PAUSED.send(sender);
                    return false;
                }
            })
            .abortIf(false)
            .async(() -> plugin.getStorage().updateProject(project))
            .execute();
	}

	@CommandDescription("Complete a project")
	@Permission(CommandPermissions.COMPLETE_PROJECT)
	@Command("complete <project>")
	public void onComplete(final @NonNull Player player,
						   @Argument(value = "project", description = "Project", suggestions = "active-project") final @NonNull Project project) {
		EpicJobs.newSharedChain("EpicJobs")
			.syncFirst(() -> {
				if (!project.getProjectStatus().equals(ProjectStatus.COMPLETE)) {
					project.setProjectStatus(ProjectStatus.COMPLETE);
					ANNOUNCE_PROJECT_COMPLETION.broadcast(project.getName());
					return true;
				} else {
					PROJECT_ALREADY_COMPLETE.send(player);
					return false;
				}
			})
			.abortIf(false)
			.async(() -> plugin.getStorage().updateProject(project))
			.execute();
	}

	@CommandDescription("Project statistics")
	@Permission(CommandPermissions.SHOW_PROJECT_STATISTICS)
	@Command("stats [project]")
	public void onStats(final @NonNull CommandSender sender,
						@Argument(value = "project", description = "Project") final @Nullable Project project) {
		if (project != null) {
			// Per-project: progress and the builders who completed its jobs (all of them).
			sendStats(sender, "Stats for " + project.getName(), project.getJobs(), project.getName(), 0);
			return;
		}

		// Global: aggregate over every job, capped to the top builders.
		final List<Job> allJobs = plugin.getJobManager().getJobs();
		final boolean anyComplete = allJobs.stream().anyMatch(job -> job.getJobStatus().equals(JobStatus.COMPLETE));
		if (!anyComplete) {
			NO_STATS_AVAILABLE.send(sender);
			return;
		}
		sendStats(sender, "Server-wide project stats", allJobs, null, 10);
	}

	private static void sendStats(final @NotNull CommandSender sender, final String title, final List<Job> jobs,
								  final @Nullable String projectName, final int limit) {
		final List<Job> completed = jobs.stream()
			.filter(job -> job.getJobStatus().equals(JobStatus.COMPLETE))
			.toList();
		final int total = jobs.size();
		final int percent = total == 0 ? 0 : completed.size() * 100 / total;

		final List<Map.Entry<UUID, Long>> leaderboard = completed.stream()
			.filter(job -> job.getClaimant() != null)
			.collect(Collectors.groupingBy(Job::getClaimant, Collectors.counting()))
			.entrySet().stream()
			.sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
			.limit(limit > 0 ? limit : Long.MAX_VALUE)
			.toList();

		final TextComponent.Builder panel = Component.text()
			.append(Component.text(title, Palette.Role.INFO.accent()).decoration(TextDecoration.BOLD, true))
			.append(Component.newline())
			.append(Component.text(completed.size() + "/" + total, Palette.Role.INFO.body()))
			.append(Component.text(" jobs complete ", Palette.MUTED))
			.append(Component.text("(" + percent + "%)", Palette.Role.INFO.body()));

		if (leaderboard.isEmpty()) {
			sender.sendMessage(Component.empty());
			sender.sendMessage(panel.build());
			if (projectName != null) {
				PROJECT_HAS_NO_COMPLETED_JOBS.send(sender, projectName);
			}
			sender.sendMessage(Component.empty());
			return;
		}

		panel.append(Component.newline()).append(Component.newline());
		int rank = 1;
		for (final Map.Entry<UUID, Long> entry : leaderboard) {
			if (rank > 1) {
				panel.append(Component.newline());
			}
			final long count = entry.getValue();
			panel.append(Component.text(rank + ". ", Palette.MUTED))
				.append(Component.text(Utils.getPlayerHolderText(entry.getKey()), Palette.Role.INFO.accent()))
				.append(Component.text(": ", Palette.MUTED))
				.append(Component.text(count + (count == 1 ? " job" : " jobs"), Palette.Role.INFO.body()));
			rank++;
		}

		sender.sendMessage(Component.empty());
		sender.sendMessage(panel.build());
		sender.sendMessage(Component.empty());
	}

}
