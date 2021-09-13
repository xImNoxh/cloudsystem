package de.polocloud.api.database.other;

import de.polocloud.api.template.base.ITemplate;

import java.util.List;

public interface IDatabaseTemplateLoader<V extends IDatabase<V>> {

    /**
     * Loads all {@link ITemplate}s from the database
     */
    List<ITemplate> loadTemplates();

    /**
     * Inserts an {@link ITemplate} into the database
     *
     * @param template the template
     */
    void insertTemplate(ITemplate template);

    /**
     * Deletes an {@link ITemplate} from database
     *
     * @param name the name of the template
     */
    void deleteTemplate(String name);

    /**
     * Saves all {@link ITemplate}s
     */
    void saveTemplates();


}
