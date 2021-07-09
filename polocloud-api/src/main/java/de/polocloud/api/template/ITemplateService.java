package de.polocloud.api.template;

import java.util.Collection;

public interface ITemplateService {

    ITemplateLoader getTemplateLoader();

    ITemplateSaver getTemplateSaver();

    ITemplate getTemplateByName(String name);

    Collection<ITemplate> getLoadedTemplates();

    void reloadTemplates();

}
