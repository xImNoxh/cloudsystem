package de.polocloud.bootstrap.template.file;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;

import java.util.Collection;
import java.util.Collections;

public class FileTemplateLoader implements ITemplateLoader {
    @Override
    public Collection<ITemplate> loadTemplates() {

        System.out.println("load templates by file");

        return Collections.EMPTY_LIST;
    }
}
