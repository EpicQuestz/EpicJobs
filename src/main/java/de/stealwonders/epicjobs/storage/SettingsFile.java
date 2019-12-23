package de.stealwonders.epicjobs.storage;

import co.aikar.idb.DatabaseOptions;
import de.stealwonders.epicjobs.EpicJobs;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SettingsFile {

    private File file;
    private YamlConfiguration yamlConfiguration;

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

    public DatabaseOptions setupHikari(FileConfiguration settings) {
        String address = settings.getString(MYSQL_INFO + "address");
        String database = settings.getString(MYSQL_INFO + "database");
        String username = settings.getString(MYSQL_INFO + "username");
        String password = settings.getString(MYSQL_INFO + "password");

        return DatabaseOptions.builder()
            .mysql(username, password, database, address.split(":")[0])
            //.driverClassName("org.mariadb.jdbc.MariaDbDataSource")
            .poolName("EpicJobs")
            .build();
    }
}
