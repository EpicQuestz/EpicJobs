package de.stealwonders.epicjobs.storage.implementation.sql.connection;

import de.stealwonders.epicjobs.EpicJobs;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    String getImplementationName();

    void init(EpicJobs plugin);

    void shutdown() throws Exception;

//    default Map<Component, Component> getMeta() {
//        return Collections.emptyMap();
//    }

    Connection getConnection() throws SQLException;
}
