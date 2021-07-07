package de.polocloud.bootstrap.template.sql;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;

import java.util.Collection;
import java.util.Collections;

public class SqlTemplateLoader implements ITemplateLoader {
    @Override
    public Collection<ITemplate> loadTemplates() {
        System.out.println("load from sql");
        return Collections.EMPTY_LIST;
    }
}
