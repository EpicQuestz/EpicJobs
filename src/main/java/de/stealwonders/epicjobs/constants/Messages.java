package de.stealwonders.epicjobs.constants;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Messages {

    SPECIFY_JOB_ID("Please specify a job id to continue."),
    JOB_DOESNT_EXIST("This job does not exist. Please check again if you entered the correct id."),
    CATEGORY_DOESNT_EXIST("This job category does not exist. Please check again if you entered the correct category name."),
    PROJECT_DOESNT_EXIST("This project does not exist. Please check again if you entered the correct project name."),
    PROJECT_NAME_NO_SPACES("A project name cannot contain spaces."),
    NO_JOBS_AVAILABLE("There are no jobs available to be claimed."),
    NO_PROJECTS_AVAILABLE("There are no active projects to participate in."),
    JOB_NOT_OPEN("This job is not open to be claimed."),
    JOB_NOT_DONE("This job is marked as done."),
    JOB_COMPLETED("Job %s has been marked as complete."),
    PLAYER_NOT_FOUND("Player %s could not be found."),
    PLAYER_HAS_NO_JOBS("You have no claimed jobs."),
    PLAYER_HASNT_CLAIMED_JOB("You haven't claimed this job."),
    PLAYER_HAS_MULITPLE_JOBS("You have more than one job. Please specify a job it to continue."),
    ANNOUNCE_JOB_TAKEN("%s has claimed job #%s"),
    ANNOUNCE_JOB_ABANDONMENT("%s has abandoned job #%s. It is free to be claimed again."),
    ANNOUNCE_JOB_DONE("%s has marked job #%s as done."),
    ANNOUNCE_JOB_REOPEN("%s has reopened job #%s."),
    ANNOUNCE_PROJECT_COMPLETION("Project '%s' has been completed!");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

    public String toString(Object... parts) {
        return String.format(message, parts);
    }

    public void send(Player player) {
        player.sendMessage(message);
    }

    public void send(CommandSender sender) {
        sender.sendMessage(message);
    }

    public void send(Player player, String replacement) {
        player.sendMessage(message.replace("%s", replacement));
    }

    public void send(Player player, Object... replacements) {
        player.sendMessage(String.format(message, replacements));
    }

    public void broadcast() {
        Bukkit.broadcastMessage(message);
    }

    public void broadcast(String replacement) {
        Bukkit.broadcastMessage(message.replace("%s", replacement));
    }

    public void broadcast(Object... replacements) {
        Bukkit.broadcastMessage(String.format(message, replacements));
    }
}
