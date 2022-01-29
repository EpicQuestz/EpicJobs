package de.stealwonders.epicjobs.storage;

import com.google.common.collect.ImmutableMap;
import de.stealwonders.epicjobs.EpicJobs;
import de.stealwonders.epicjobs.storage.implementation.StorageImplementation;
import de.stealwonders.epicjobs.storage.implementation.sql.SqlStorage;
import de.stealwonders.epicjobs.storage.implementation.sql.connection.hikari.MariaDbConnectionFactory;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class StorageFactory {

    private final EpicJobs plugin;

    public StorageFactory(EpicJobs plugin) {
        this.plugin = plugin;
    }

    public Storage getInstance() {
        plugin.getLogger().info("Loading storage provider... [mariadb]");
        Storage storage = new Storage(plugin, createNewImplementation());
        try {
            storage.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storage;
    }

    private StorageImplementation createNewImplementation() {

        Configuration config = plugin.getConfig();

        int maxPoolSize = config.getInt("data.pool-settings.maximum-pool-size", config.getInt("data.pool-size", 10));
        int minIdle = config.getInt("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = config.getInt("data.pool-settings.maximum-lifetime", 1800000);
        int keepAliveTime = config.getInt("data.pool-settings.keepalive-time", 0);
        int connectionTimeout = config.getInt("data.pool-settings.connection-timeout", 5000);

        Map<String, String> direct = ImmutableMap.of();
        ConfigurationSection section = config.getConfigurationSection("data.pool-settings.properties");
        if (section != null) {
            direct = new HashMap<>();
            for (String key : section.getKeys(false)) {
                direct.put(key, section.getString(key));
            }
        }

        Map<String, String> props = ImmutableMap.copyOf(direct);

        StorageCredentials storageCredentials = new StorageCredentials(
                config.getString("data.address", null),
                config.getString("data.database", null),
                config.getString("data.username", null),
                config.getString("data.password", null),
                maxPoolSize, minIdle, maxLifetime, keepAliveTime, connectionTimeout, props
        );

        return new SqlStorage(plugin, new MariaDbConnectionFactory(storageCredentials));
    }

}
