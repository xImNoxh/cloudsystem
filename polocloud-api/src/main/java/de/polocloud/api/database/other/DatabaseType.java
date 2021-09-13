package de.polocloud.api.database.other;

import de.polocloud.api.database.other.files.FileDatabase;
import de.polocloud.api.database.other.mongodb.MongoDBDatabase;
import de.polocloud.api.database.other.mysql.MySQLDatabase;
import de.polocloud.api.util.gson.PoloHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum DatabaseType {

    MYSQL(MySQLDatabase.class),
    MONGODB(MongoDBDatabase.class),
    FILES(FileDatabase.class);

    /**
     * The class of this type extending {@link IDatabase}
     */
    private final Class<? extends IDatabase<?>> dbClass;


    /**
     * Creates a new {@link IDatabase} instance
     * based on the class of this {@link DatabaseType}
     */
    public IDatabase<?> getDatabase() {
        return PoloHelper.getInstance(dbClass);
    }
}
