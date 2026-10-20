package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.EpicJobs;
import com.epicquestz.epicjobs.constants.Palette.Role;
import com.epicquestz.epicjobs.job.Job;
import com.epicquestz.epicjobs.project.Project;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

/**
 * Posts job updates as embeds to the Discord webhook configured at {@code discord.webhook-url} in settings.yml.
 * Does nothing when the URL is blank.
 */
public final class DiscordWebhook {

    /** Routine events stay neutral; only setbacks and completions get colour, matching the in-game roles. */
    public enum JobEvent {
        CREATED("created", Role.INFO),
        CLAIMED("claimed", Role.INFO),
        ABANDONED("abandoned", Role.WARNING),
        DONE("marked done", Role.INFO),
        COMPLETED("completed", Role.SUCCESS);

        private final String verb;
        private final int color;

        JobEvent(final String verb, final Role role) {
            this.verb = verb;
            this.color = role.body().value();
        }
    }
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private DiscordWebhook() {
    }

    public static void job(final JobEvent event, final Player actor, final Job job) {
        final JsonObject embed = embed(actor, "Job #" + job.getId() + " " + event.verb, event.color);
        embed.addProperty("description", job.getDescription());

        final JsonArray fields = new JsonArray();
        fields.add(field("Project", job.getProject().getName()));
        fields.add(field("Category", job.getJobCategory().getName()));
        final UUID claimant = job.getClaimant();
        if (claimant != null && !claimant.equals(actor.getUniqueId())) {
            fields.add(field("Claimant", Utils.getPlayerHolderText(claimant)));
        }
        embed.add("fields", fields);
        post(embed);
    }

    public static void projectCompleted(final Player actor, final Project project) {
        final JsonObject embed = embed(actor, "Project " + project.getName() + " completed", Role.SUCCESS.body().value());

        final JsonArray fields = new JsonArray();
        if (!project.isLeader(actor.getUniqueId())) {
            fields.add(field("Leader", Utils.getPlayerHolderText(project.getLeader())));
        }
        embed.add("fields", fields);
        post(embed);
    }

    private static JsonObject embed(final Player actor, final String title, final int color) {
        final JsonObject author = new JsonObject();
        author.addProperty("name", actor.getName());
        author.addProperty("icon_url", "https://mc-heads.net/avatar/" + actor.getUniqueId() + "/64");

        final JsonObject embed = new JsonObject();
        embed.add("author", author);
        embed.addProperty("title", title);
        embed.addProperty("color", color);
        return embed;
    }

    private static JsonObject field(final String name, final String value) {
        final JsonObject field = new JsonObject();
        field.addProperty("name", name);
        field.addProperty("value", value);
        field.addProperty("inline", true);
        return field;
    }

    private static void post(final JsonObject embed) {
        final EpicJobs plugin = EpicJobs.get();
        final String url = plugin.getSettingsFile().getConfiguration().getString("discord.webhook-url", "");
        if (url.isBlank()) {
            return;
        }

        final JsonArray embeds = new JsonArray();
        embeds.add(embed);
        final JsonObject allowedMentions = new JsonObject();
        allowedMentions.add("parse", new JsonArray()); // never ping @everyone/roles from user-written text
        final JsonObject body = new JsonObject();
        body.add("embeds", embeds);
        body.add("allowed_mentions", allowedMentions);

        final HttpRequest request;
        try {
            request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        } catch (final IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid discord.webhook-url: " + e.getMessage());
            return;
        }

        CLIENT.sendAsync(request, HttpResponse.BodyHandlers.discarding())
            .whenComplete((response, error) -> {
                if (error != null) {
                    plugin.getLogger().warning("Discord webhook failed: " + error.getMessage());
                } else if (response.statusCode() >= 300) {
                    plugin.getLogger().warning("Discord webhook returned HTTP " + response.statusCode());
                }
            });
    }

}
