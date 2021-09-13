package de.polocloud.api.database.other.mysql;

import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import de.polocloud.api.template.base.ITemplate;

import java.util.List;

public class MySQLDatabaseTemplateLoader implements IDatabaseTemplateLoader<MySQLDatabase> {

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
