package de.polocloud.bootstrap.template;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.api.template.ITemplateService;

public class SimpleTemplateService implements ITemplateService {


    private ITemplateLoader templateLoader;
    private ITemplateSaver templateSaver;

    public SimpleTemplateService(CloudAPI cloudAPI, TemplateStorage templateStorage) {

        this.templateLoader = cloudAPI.getGuice().getInstance(templateStorage.getTemplateLoader());
        this.templateSaver = cloudAPI.getGuice().getInstance(templateStorage.getTemplateServer());

    }

    @Override
    public ITemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }

    @Override
    public ITemplateSaver getTemplateSaver() {
        return this.templateSaver;
    }

}
