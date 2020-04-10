package de.stealwonders.epicjobs.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Messages {

    // Jobs List & General
    JOB_DOESNT_EXIST("This job does not exist. Please check again if you entered the correct id."),
    NO_JOBS_AVAILABLE("There aren't §bany§f jobs available."),
    PLAYER_HAS_NO_JOBS("You have no claimed jobs."), // could maybe be replaced with NO_JOBS_AVAILABLE
    PLAYER_HAS_MULITPLE_JOBS("You have more than one job. Please specify a job it to continue."),
    PLAYER_HASNT_CLAIMED_JOB("You haven't claimed this job."),

    // Jobs Claim
    ANNOUNCE_JOB_TAKEN("%s has claimed job #%s"),
    JOB_NOT_OPEN("This job is not open to be claimed."),

    // Jobs Abandon
    ANNOUNCE_JOB_ABANDONMENT("%s has abandoned job #%s. It is free to be claimed again."),
    JOB_CANT_BE_ABANDONED("You can only abandon jobs that haven't been completed."),

    // Jobs Done
    ANNOUNCE_JOB_DONE("%s has marked job #%s as done."),
    JOB_HAS_TO_BE_ACTIVE("The job has to be active to be marked done."),
    PLAYER_HAS_NO_ACTIVE_JOBS("You have §bno active§f jobs."),

    // Jobs Complete
    JOB_COMPLETED("Job %s has been marked as complete."),
    JOB_CANT_BE_COMPLETE("A job has to be marked 'done' to complete."),

    // Jobs Reopen
    ANNOUNCE_JOB_REOPEN("%s has reopened job #%s."),
    JOB_NOT_DONE("This job is marked as done."), //????

    // Jobs Unassign
    HAS_ASSIGNED_JOB("You have assigned %s to job #%s"),

    // Jobs Assign
    JOB_CANT_BE_ASSIGNED("You can only assign untaken jobs to a player."),
    HAS_BEEN_ASSIGNED_JOB("You have been assigned job #%s"),
    JOB_CANT_BE_UNASSIGNED("You can only unassign uncomplete jobs taken by a player."),

    // Jobs Create
    CREATING_JOB("Creating job"), //actionbar
    SUCCESSFULLY_CREATED_JOB("§aSuccessfully created job with id #%s"),

    // Jobs Remove
    REMOVING_JOB("Removing job #%s"), //actionbar
    SUCCESSFULLY_REMOVED_JOB("§aSuccessfully deleted job."),

    // Project List & General
    PROJECT_DOESNT_EXIST("This project does not exist. Please check again if you entered the correct project name."),
    NO_PROJECTS_AVAILABLE("There are §bno§f active projects to participate in."),
    PROJECT_ALREADY_COMPLETE("This project is already marked as complete."),

    // Create Project
    CREATING_PROJECT("Creating project %s"), //actionbar
    SUCCESSFULLY_CREATED_PROJECT("§aSuccessfully created project with id #%s"),
    CANT_CREATE_PROJECT("Cannot create a project with duplicate name."),

    // Project Edit
    PLAYER_NOT_FOUND("Player %s could not be found."),

    // Project Complete
    ANNOUNCE_PROJECT_COMPLETION("Project '%s' has been completed!");

//    /*
//     * Old unused - maybe needed in the future
//     */
//
//    SPECIFY_JOB_ID("Please specify a job id to continue."),
//    CATEGORY_DOESNT_EXIST("This job category does not exist. Please check again if you entered the correct category name."),
//    PROJECT_NAME_NO_SPACES("A project name cannot contain spaces."),
//    PROJECT_ALREADY_COMPLETE("The project you want to create a job for has already been complete."),

    private String message;

    Messages(final String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

    public String toString(final Object... parts) {
        return String.format(message, parts);
    }

    public void send(final Player player) {
        player.sendMessage(message);
    }

    public void send(final CommandSender sender) {
        sender.sendMessage(message);
    }

    public void send(final Player player, final String replacement) {
        player.sendMessage(message.replace("%s", replacement));
    }

    public void send(final Player player, final Object... replacements) {
        player.sendMessage(String.format(message, replacements));
    }

    public void sendActionbar(final Player player) {
        player.sendActionBar(message);
    }

    public void sendActionbar(final Player player, final String replacement) {
        player.sendActionBar(message.replace("%s", replacement));
    }

    public void sendActionbar(final Player player, final Object... replacements) {
        player.sendActionBar(String.format(message, replacements));
    }

    public void broadcast() {
        Bukkit.broadcastMessage(message);
    }

    public void broadcast(final String replacement) {
        Bukkit.broadcastMessage(message.replace("%s", replacement));
    }

    public void broadcast(final Object... replacements) {
        Bukkit.broadcastMessage(String.format(message, replacements));
    }

}
