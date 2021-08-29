package de.polocloud.api.template.loading;

import de.polocloud.api.template.base.ITemplate;

public interface ITemplateSaver {

    /**
     * Saves an {@link ITemplate}
     *
     * @param template the template
     */
    void save(ITemplate template);

}
