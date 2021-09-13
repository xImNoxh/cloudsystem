package de.polocloud.api.database.other.mongodb;

import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import de.polocloud.api.template.base.ITemplate;

import java.util.List;

public class MongoDBDatabaseTemplateLoader implements IDatabaseTemplateLoader<MongoDBDatabase> {

    @Override
    public List<ITemplate> loadTemplates() {
        return null;
    }

    @Override
    public void insertTemplate(ITemplate template) {

    }

    @Override
    public void deleteTemplate(String name) {

    }

    @Override
    public void saveTemplates() {

    }
}
