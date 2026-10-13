package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.EpicJobs;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Posts job updates to the Discord webhook configured at {@code discord.webhook-url} in settings.yml.
 * Does nothing when the URL is blank.
 */
public final class DiscordWebhook {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private DiscordWebhook() {
    }

    public static void send(final Component message) {
        final EpicJobs plugin = EpicJobs.get();
        final String url = plugin.getSettingsFile().getConfiguration().getString("discord.webhook-url", "");
        if (url.isBlank()) {
            return;
        }

        final JsonObject body = new JsonObject();
        body.addProperty("content", PlainTextComponentSerializer.plainText().serialize(message));
        final JsonObject allowedMentions = new JsonObject();
        allowedMentions.add("parse", new JsonArray()); // never ping @everyone/roles from project names
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
