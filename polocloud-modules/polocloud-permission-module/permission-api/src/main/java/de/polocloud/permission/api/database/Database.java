package de.polocloud.permission.api.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Redis
public interface Database {

    void connect(@NotNull DatabaseCredentials credentials, @Nullable String usage);

    default void connect(@NotNull DatabaseCredentials credentials) {
        connect(credentials, null);
    }

    boolean isConnected();

}
