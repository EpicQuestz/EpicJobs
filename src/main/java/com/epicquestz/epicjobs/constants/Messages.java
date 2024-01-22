package com.epicquestz.epicjobs.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Messages {

    // Jobs List & General
    JOB_DOESNT_EXIST("§cThis job does not exist. Did you enter the correct job ID?"),
    NO_JOBS_AVAILABLE("§cThere are no jobs available! Check back later."),
    PLAYER_HAS_NO_JOBS("§cYou have no claimed jobs!\n§6Use /job check to see if there are any available to claim."),
    PLAYER_HAS_MULITPLE_JOBS("§cYou have more than one job.\n§6Please specify a job to continue."),
    PLAYER_HASNT_CLAIMED_JOB("§cYou have not claimed this job."),

    // Jobs Claim
    ANNOUNCE_JOB_TAKEN("§a%s has claimed job §b#%s."),
    JOB_NOT_OPEN("§cThis job is not available to be claimed."),

    // Jobs Abandon
    ANNOUNCE_JOB_ABANDONMENT("§c%s has abandoned job §b#%s.\n§aIt is available to be claimed again."),
    JOB_CANT_BE_ABANDONED("§cYou can only abandon jobs that are incomplete."),

    // Jobs Teleport
    PLAYER_JOB_TELEPORT("§eTeleporting to job site #%s"),

    // Jobs Done
    ANNOUNCE_JOB_DONE("§a%s has marked job §b#%s§a as done."),
    JOB_HAS_TO_BE_ACTIVE("§cThe job has to be active to be marked done."),
    PLAYER_HAS_NO_ACTIVE_JOBS("§cYou have no active jobs."),

    // Jobs Complete
    JOB_COMPLETED("§aJob §b#%s§a has been marked as complete."),
    JOB_CANT_BE_COMPLETE("§cA job has to be marked done to complete."),

    // Jobs Reopen
    ANNOUNCE_JOB_REOPEN("§6%s has reopened job §b#%s."),
    JOB_REOPEN("§aRe-opened job §b#%s§a for player %s to make edits."),
    JOB_NOT_DONE("§cThis job is not marked as done or complete."),

    // Jobs Un-assign
    JOB_CANT_BE_UNASSIGNED("§cYou can only un-assign incomplete jobs taken by a player."),

    // Jobs Assign,
    HAS_ASSIGNED_JOB("§6You have assigned %s to job §b#%s."),
    JOB_CANT_BE_ASSIGNED("§cYou can only assign available jobs to a player."),
    HAS_BEEN_ASSIGNED_JOB("§6You have been assigned job §b#%s."),

    // Jobs Create
    CREATING_JOB("§6Creating job..."), //actionbar
    SUCCESSFULLY_CREATED_JOB("§aSuccessfully created job with id §b#%s."),

    // Jobs Remove
    REMOVING_JOB("§6Removing job #%s..."), //actionbar
    SUCCESSFULLY_REMOVED_JOB("§aSuccessfully deleted job."),

    // Project List & General
    PROJECT_DOESNT_EXIST("§cThis project does not exist. Did you enter the correct project name?"),
    NO_PROJECTS_AVAILABLE("§cThere are no active projects to participate in."),
    PROJECT_ALREADY_COMPLETE("§cThis project is already marked as complete."),

    // Create Project
    CREATING_PROJECT("§6Creating project %s..."), //actionbar
    SUCCESSFULLY_CREATED_PROJECT("§aSuccessfully created project with id #%s."),
    CANT_CREATE_PROJECT("§cCannot create a project with duplicate name."),

    // Project Edit
    PLAYER_NOT_FOUND("§cPlayer %s could not be found."),

    // Project Teleport
    PLAYER_PROJECT_TELEPORT("§eTeleporting to project site %s"),

    // Project Complete
    ANNOUNCE_PROJECT_COMPLETION("§aProject %s has been completed!"),

    // Profile
    MISSING_PROFILE("§cYour EpicJobs profile could not be found. Please contact an administrator.");

//    /*
//     * Old unused - maybe needed in the future
//     */
//
//    SPECIFY_JOB_ID("Please specify a job id to continue."),
//    CATEGORY_DOESNT_EXIST("This job category does not exist. Please check again if you entered the correct category name."),
//    PROJECT_NAME_NO_SPACES("A project name cannot contain spaces."),
//    PROJECT_ALREADY_COMPLETE("The project you want to create a job for has already been complete."),

    private final String message;

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
