package de.polocloud.api.template.loading;

import de.polocloud.api.template.base.ITemplate;

import java.util.List;

public interface ITemplateLoader {

    /**
     * Loads all saved {@link ITemplate}s into the cache
     *
     * @return collection of loaded templates
     */
    List<ITemplate> loadTemplates();

}
