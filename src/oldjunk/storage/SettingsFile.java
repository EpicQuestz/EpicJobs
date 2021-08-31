package de.stealwonders.epicjobs.storage;

import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.EpicJobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SettingsFile {

    private final File file;
    private final YamlConfiguration yamlConfiguration;

    private static final String MYSQL_INFO = "mysql.";

    public SettingsFile(final EpicJobs plugin) {
        file = new File(plugin.getDataFolder(), "settings.yml");
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        if (!yamlConfiguration.isConfigurationSection("mysql")) {
            yamlConfiguration.set(MYSQL_INFO + "address", "localhost:3306");
            yamlConfiguration.set(MYSQL_INFO + "database", "epicjobs");
            yamlConfiguration.set(MYSQL_INFO + "username", "root");
            yamlConfiguration.set(MYSQL_INFO + "password", "123SuperSecret321");
            save();
        }
    }

    public YamlConfiguration getConfiguration() {
        return yamlConfiguration;
    }

    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static final String DATA_SOURCE_CLASS = "org.mariadb.jdbc.MySQLDataSource";

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30); // 30 Minutes
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10); // 10 seconds
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10); // 10 seconds

    public HikariDataSource setupHikari(final HikariDataSource hikariDataSource, final FileConfiguration settings) {
        final String address = settings.getString(MYSQL_INFO + "address");
        final String database = settings.getString(MYSQL_INFO + "database");
        final String username = settings.getString(MYSQL_INFO + "username");
        final String password = settings.getString(MYSQL_INFO + "password");

        hikariDataSource.setPoolName("EpicJobs");

        hikariDataSource.setDataSourceClassName(DATA_SOURCE_CLASS);
        hikariDataSource.addDataSourceProperty("serverName", address.split(":")[0]);
        hikariDataSource.addDataSourceProperty("port", address.split(":")[1]);
        hikariDataSource.addDataSourceProperty("databaseName", database);
        hikariDataSource.addDataSourceProperty("user", username);
        hikariDataSource.addDataSourceProperty("password", password);

        hikariDataSource.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        hikariDataSource.setMinimumIdle(MINIMUM_IDLE);

        hikariDataSource.setMaxLifetime(MAX_LIFETIME);
        hikariDataSource.setConnectionTimeout(CONNECTION_TIMEOUT);
        hikariDataSource.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

        // ensure we use unicode (this calls #setProperties, a hack for the mariadb driver)
        hikariDataSource.addDataSourceProperty("properties", "useUnicode=true;characterEncoding=utf8");

        return hikariDataSource;
    }

}
