package de.stealwonders.epicjobs.command.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import de.stealwonders.epicjobs.model.project.Project;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TeleportProjectCommand {

    @CommandDescription("Teleport to a project site")
    @CommandMethod("project|projects teleport|tp <project>") //todo: make alternative for subcommands
    public void onListAll(final @NonNull Player player,
                          @Argument(value = "project", description = "Project")
                          final @NonNull Project project) {
        player.teleportAsync(project.getLocation());
    }

}
