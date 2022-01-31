package de.stealwonders.epicjobs.command.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.command.CommandPermissions;
import de.stealwonders.epicjobs.model.project.Project;
import me.lucko.helper.promise.Promise;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

@CommandPermission(CommandPermissions.MODIFY_PROJECTS)
public record EditProjectCommand(EpicJobs plugin) {

    @CommandDescription("Edits a projects name")
    @CommandMethod("project|projects edit <project> name <name>")
    public void onEditName(final @NonNull CommandSender commandSender,
                           @Argument(value = "project", description = "Project")
                           final @NonNull Project project,
                           @Argument(value = "name", description = "Name")
                           final @NonNull String name) {
        project.setName(name); // todo: check for duplicate
        Promise<Void> promise = plugin.getStorage().saveProject(project);
        promise.thenAcceptSync(v -> commandSender.sendMessage("Set project name to '" + project.getName() + "'"));
    }

    @CommandDescription("Edits a projects location site")
    @CommandMethod("project|projects edit <project> location")
    public void onEditLocation(final @NonNull Player player,
                               @Argument(value = "project", description = "Project")
                               final @NonNull Project project) {
        project.setLocation(player.getLocation());
        Promise<Void> promise = plugin.getStorage().saveProject(project);
        promise.thenAcceptSync(v -> player.sendMessage("Updated project location site to your location"));
    }

    @ProxiedBy("addleader")
    @CommandDescription("Edits a projects leaders")
    @CommandMethod("project|projects edit <project> leader add <leader>")
    public void onEditLeader(final @NonNull Player player,
                             @Argument(value = "project", description = "Project")
                             final @NonNull Project project,
                             @Argument(value = "leader", description = "Leader") // todo: offlineplayer
                             final @NonNull Player leader) {
        project.addLeader(leader);
        Promise<Void> promise = plugin.getStorage().saveProject(project);
        promise.thenAcceptSync(v -> player.sendMessage("Added leader " + leader.getName()));
    }

//        @Subcommand("edit leader")
//        @CommandCompletion("@project @players")
//        @CommandPermission("epicjobs.command.project.edit")
//        public void onEditLeader(final Player player, final Project project, final OnlinePlayer leader) {
//            project.setLeader(leader.getPlayer());
//            player.sendMessage("Set project leader to " + leader.getPlayer().getName());
//            plugin.getStorage().updateProject(project);
//        }

}
