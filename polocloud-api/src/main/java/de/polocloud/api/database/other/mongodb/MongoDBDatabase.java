package de.polocloud.api.database.other.mongodb;

import de.polocloud.api.database.IDatabaseManager;
import de.polocloud.api.database.other.DatabaseType;
import de.polocloud.api.database.other.IDatabase;
import de.polocloud.api.database.other.IDatabasePropertyLoader;
import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MongoDBDatabase implements IDatabase<MongoDBDatabase> {

    /**
     * The template database loader
     */
    private final IDatabaseTemplateLoader<MongoDBDatabase> templateLoader;

    /**
     * The property database loader
     */
    private final IDatabasePropertyLoader<MongoDBDatabase> propertyLoader;

    /**
     * The manager (also seen as parent)
     */
    private IDatabaseManager manager;

    public MongoDBDatabase() {
        this.templateLoader = new MongoDBDatabaseTemplateLoader();
        this.propertyLoader = new MongoDBDatabasePropertyLoader();
    }
    @Override
    public DatabaseType getType() {
        return DatabaseType.MONGODB;
    }

    @Override
    public void connect() throws Exception {


        //TODO: CONNECT MONGODB
        this.propertyLoader.loadProperties();
        this.templateLoader.loadTemplates();
    }

    @Override
    public void close() throws Exception {
        //TODO: SHUTDOWN MONGODB
        this.propertyLoader.saveAll();
        this.templateLoader.saveTemplates();
    }
}
