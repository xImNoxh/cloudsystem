package de.polocloud.api.template;

public interface ITemplateService {

    ITemplateLoader getTemplateLoader();

    ITemplateSaver getTemplateSaver();

    ITemplate getTemplateByName(String name);

    void reloadTemplates();

}
