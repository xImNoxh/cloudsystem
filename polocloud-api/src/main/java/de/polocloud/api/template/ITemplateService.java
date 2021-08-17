package de.polocloud.api.template;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface ITemplateService {

    /**
     * Gets the {@link ITemplateLoader} to load all templates
     */
    ITemplateLoader getTemplateLoader();

    /**
     * Gets the {@link ITemplateSaver} to save all templates
     */
    ITemplateSaver getTemplateSaver();

    /**
     * Gets an {@link ITemplate} by its name
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param name the name of the template
     */
    CompletableFuture<ITemplate> getTemplateByName(String name);

    /**
     * Gets a collection of all loaded {@link ITemplate}s
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    CompletableFuture<Collection<ITemplate>> getLoadedTemplates();

    /**
     * Reloads all templates and refreshes the cache
     */
    void reloadTemplates();

}
