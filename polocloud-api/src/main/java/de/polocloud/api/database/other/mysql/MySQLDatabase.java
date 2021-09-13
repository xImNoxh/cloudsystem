package de.polocloud.api.database.other.mysql;

import de.polocloud.api.database.other.DatabaseType;
import de.polocloud.api.database.other.IDatabase;
import de.polocloud.api.database.other.IDatabasePropertyLoader;
import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import lombok.Getter;

@Getter
public class MySQLDatabase implements IDatabase<MySQLDatabase> {

    /**
     * The template database loader
     */
    private final IDatabaseTemplateLoader<MySQLDatabase> templateLoader;

    /**
     * The property database loader
     */
    private final IDatabasePropertyLoader<MySQLDatabase> propertyLoader;

    public MySQLDatabase() {
        this.templateLoader = new MySQLDatabaseTemplateLoader();
        this.propertyLoader = new MySQLDatabasePropertyLoader();
    }
    @Override
    public DatabaseType getType() {
        return DatabaseType.MYSQL;
    }

    @Override
    public void connect() throws Exception {
        //TODO: CONNECT MYSQL
        this.propertyLoader.loadProperties();
        this.templateLoader.loadTemplates();
    }

    @Override
    public void close() throws Exception {
        //TODO: SHUTDOWN MYSQL
        this.propertyLoader.saveAll();
        this.templateLoader.saveTemplates();
    }
}
