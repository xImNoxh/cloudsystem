package de.polocloud.database;

import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private Connection connection;

    public DatabaseConnector(String host, String user, String password, String database, int port) {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&autoReconnect=true", user, password);
            Logger.log(LoggerType.INFO, "Successfully connected to mysql server.");
        } catch (SQLException exception) {
            exception.printStackTrace();
            Logger.log(LoggerType.INFO, "Couldn’t connect to the mysql service.");
            Logger.log(LoggerType.INFO, "You can change the settings in the config.json.");
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
