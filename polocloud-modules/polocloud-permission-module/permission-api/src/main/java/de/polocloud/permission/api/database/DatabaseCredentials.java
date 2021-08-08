package de.polocloud.permission.api.database;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.polocloud.permission.api.database.gson.GsonSerializable;
import de.polocloud.permission.api.database.gson.JsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class DatabaseCredentials implements GsonSerializable {
    static DatabaseCredentials deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("hostname"));
        Preconditions.checkArgument(object.has("port"));
        Preconditions.checkArgument(object.has("password"));

        String hostname = object.get("hostname").getAsString();
        String username = null;
        if (object.has("username")) {
            username = object.get("username").getAsString();
        }
        int port = object.get("port").getAsInt();
        String database = null;
        if (object.has("database")) {
            database = object.get("database").getAsString();
        }
        String password = object.get("password").getAsString();
        return of(hostname, username, database, port, password);
    }

    public static DatabaseCredentials of(@NotNull String hostname, @Nullable String username, @Nullable String database, int port, String password) {
        return new DatabaseCredentials(hostname, username, database, port, password);
    }

    private final String hostname;
    private final String username;
    private final String database;
    private final int port;
    private final String password;

    private DatabaseCredentials(@NotNull String hostname, @Nullable String username, @Nullable String database, int port, String password) {
        this.hostname = hostname;
        this.username = username;
        this.database = database;
        this.port = port;
        this.password = password;
    }

    @NotNull
    public String getHostname() {
        return hostname;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.object()
            .add("hostname", hostname)
            .add("username", username)
            .add("database", database)
            .add("port", port)
            .add("password", password)
            .build();
    }
}
