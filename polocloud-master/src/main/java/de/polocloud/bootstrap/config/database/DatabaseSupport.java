package de.polocloud.bootstrap.config.database;

public class DatabaseSupport {

    private boolean use = false;

    private String hostname = "localhost";
    private String database = "polocloud";
    private String username = "root";
    private String password = "pw123";
    private int port = 3306;

    public String getHostname() {
        return hostname;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUse() {
        return use;
    }

    public int getPort() {
        return port;
    }
}
