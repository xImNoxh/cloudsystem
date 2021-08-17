package de.polocloud.api.template;

import java.util.Collection;

public interface ITemplateLoader {

    /**
     * Loads all saved {@link ITemplate}s into the cache
     *
     * @return collection of loaded templates
     */
    Collection<ITemplate> loadTemplates();

}
