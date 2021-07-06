package de.polocloud.database;

import de.polocloud.database.functions.SqlFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseExecutor {

    private final DatabaseConnector databaseConnector;
    private static DatabaseExecutor databaseExecutor;

    public DatabaseExecutor(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        databaseExecutor = this;
    }

    public <T> T executeQuery(String query, SqlFunction<ResultSet, T> function, T defaultValue) {
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(query)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return function.apply(resultSet);
            } catch (Exception throwable) {
                return defaultValue;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return defaultValue;
    }

    public int executeUpdate(String query) {
        try (PreparedStatement preparedStatement = databaseConnector.getConnection().prepareStatement(query)) {
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }
    }

    public static DatabaseExecutor getDatabaseExecutor() {
        return databaseExecutor;
    }

    public Connection getConnection() {
        return databaseConnector.getConnection();
    }


}
