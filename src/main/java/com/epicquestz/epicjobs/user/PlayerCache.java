package com.epicquestz.epicjobs.user;

import com.epicquestz.epicjobs.EpicJobs;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cache of player name &lt;-&gt; UUID mappings, backed by the {@code player}
 * database table. Lets the plugin resolve players that are currently offline (for example
 * when assigning a job to someone who has played here before).
 */
public class PlayerCache {

    private final EpicJobs plugin;

    private final Map<UUID, String> namesByUuid = new ConcurrentHashMap<>();
    private final Map<String, UUID> uuidsByName = new ConcurrentHashMap<>(); // key: lower-cased name

    public PlayerCache(final EpicJobs plugin) {
        this.plugin = plugin;
    }

    public void firstLoad() {
        plugin.getStorage().loadAllPlayers();
    }

    /**
     * Stores a mapping in memory only. Used when loading from the database.
     */
    public void cache(final UUID uuid, final String name) {
        namesByUuid.put(uuid, name);
        uuidsByName.put(name.toLowerCase(Locale.ROOT), uuid);
    }

    public @Nullable UUID getUuid(final String name) {
        return uuidsByName.get(name.toLowerCase(Locale.ROOT));
    }

    public @Nullable String getName(final UUID uuid) {
        return namesByUuid.get(uuid);
    }

    /**
     * @return an immutable snapshot of all known player names (for suggestions).
     */
    public Collection<String> getNames() {
        return namesByUuid.values();
    }

}
