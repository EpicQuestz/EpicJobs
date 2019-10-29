package de.stealwonders.epicjobs.storage;

import com.zaxxer.hikari.HikariDataSource;
import de.stealwonders.epicjobs.EpicJobs;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

    public void setupHikari(HikariDataSource hikariDataSource, FileConfiguration settings) {
        String address = settings.getString(MYSQL_INFO + "address");
        String database = settings.getString(MYSQL_INFO + "database");
        String username = settings.getString(MYSQL_INFO + "username");
        String password = settings.getString(MYSQL_INFO + "password");

        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariDataSource.addDataSourceProperty("serverName", address.split(":")[0]);
        hikariDataSource.addDataSourceProperty("port", address.split(":")[1]);
        hikariDataSource.addDataSourceProperty("databaseName", database);
        hikariDataSource.addDataSourceProperty("user", username);
        hikariDataSource.addDataSourceProperty("password", password);
        hikariDataSource.setMaxLifetime(10* 60 * 1000);

        Connection connection = null;

        try {
            connection = hikariDataSource.getConnection();

            //PreparedStatement preparedStatement = connection.prepareStatement(/* todo create tables */);
            //preparedStatement.execute();
            //preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.shutdown();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
