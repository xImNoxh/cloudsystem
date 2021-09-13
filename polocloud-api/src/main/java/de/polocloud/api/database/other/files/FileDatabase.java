package de.polocloud.api.database.other.files;

import de.polocloud.api.database.other.DatabaseType;
import de.polocloud.api.database.other.IDatabase;
import de.polocloud.api.database.other.IDatabasePropertyLoader;
import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import lombok.Getter;

@Getter
public class FileDatabase implements IDatabase<FileDatabase> {

    /**
     * The template database loader
     */
    private final IDatabaseTemplateLoader<FileDatabase> templateLoader;

    /**
     * The property database loader
     */
    private final IDatabasePropertyLoader<FileDatabase> propertyLoader;

    public FileDatabase() {
        this.templateLoader = new FileDatabaseTemplateLoader();
        this.propertyLoader = new FileDatabasePropertyLoader();
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.FILES;
    }

    @Override
    public void connect() throws Exception {
        this.propertyLoader.loadProperties();
        this.templateLoader.loadTemplates();
    }

    @Override
    public void close() throws Exception {
        this.propertyLoader.saveAll();
        this.templateLoader.saveTemplates();
    }
}
