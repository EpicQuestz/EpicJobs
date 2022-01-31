package de.stealwonders.epicjobs.command.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.model.project.Project;
import me.lucko.helper.promise.Promise;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

@CommandPermission(CommandPermissions.CREATE_PROJECTS)
public record CreateProjectCommand(EpicJobs plugin) {

    @CommandDescription("Creates a project")
    @CommandMethod("project|projects create <name> [leader]")
    public void onCommand(final @NonNull Player player,
                          @Argument(value = "name", description = "Name")
                          final @NonNull String name,
                          @Argument(value = "leader", description = "Leader") // todo: add support for multiple offline-player leaders
                          final @NonNull OfflinePlayer leader
    ) {
        Promise<Project> promise = plugin.getStorage().createAndLoadProject(name, player); // todo: check for duplicate
        promise.thenAcceptSync(project -> {
            plugin.getProjectManager().addProject(project);
            player.sendMessage("Yo dawg, you just created a project named '" + project.getName() + "'");
        });
    }

}
