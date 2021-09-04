package de.polocloud.api.template;

import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.loading.ITemplateLoader;
import de.polocloud.api.template.loading.ITemplateSaver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITemplateManager {

    /**
     * Gets the {@link ITemplateLoader} to load all templates
     */
    ITemplateLoader getTemplateLoader();

    /**
     * Gets the {@link ITemplateSaver} to save all templates
     */
    ITemplateSaver getTemplateSaver();

    /**
     * Loads all templates
     *
     * @param storage the storage
     */
    void loadTemplates(TemplateStorage storage);

    /**
     * Sets the cached templates
     *
     * @param templates the templates
     */
    void setCachedObjects(List<ITemplate> templates);

    /**
     * Copies an {@link IGameServer} to its template
     *
     * @param gameServer the server
     * @param type the type
     */
    void copyServer(IGameServer gameServer, Type type);

    /**
     * Gets an {@link ITemplate} by its name
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     *
     * @param name the name of the template
     */
    ITemplate getTemplate(String name);

    /**
     * Gets a collection of all loaded {@link ITemplate}s
     * and returns a {@link CompletableFuture} to handle the response
     * async or sync
     */
    List<ITemplate> getTemplates();

    /**
     * Reloads all templates and refreshes the cache
     */
    void reloadTemplates();


    enum Type {
        WORLD,
        ENTIRE
    }

}
