package de.polocloud.bootstrap.template.sql;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Collections;

public class SqlTemplateLoader implements ITemplateLoader {
    @Override
    public Collection<ITemplate> loadTemplates() {
        throw new NotImplementedException();
    }
}
