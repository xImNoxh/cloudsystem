package de.polocloud.api.template;

public interface ITemplateService {

    ITemplateLoader getTemplateLoader();

    ITemplateSaver getTemplateSaver();

}
