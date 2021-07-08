package de.polocloud.bootstrap.template;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.api.template.ITemplateService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleTemplateService implements ITemplateService {


    private ITemplateLoader templateLoader;
    private ITemplateSaver templateSaver;

    private Collection<ITemplate> templateList = new ArrayList<>();

    public SimpleTemplateService(CloudAPI cloudAPI, TemplateStorage templateStorage) {

        this.templateLoader = cloudAPI.getGuice().getInstance(templateStorage.getTemplateLoader());
        this.templateSaver = cloudAPI.getGuice().getInstance(templateStorage.getTemplateServer());

        this.reloadTemplates();

    }

    @Override
    public ITemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }

    @Override
    public ITemplateSaver getTemplateSaver() {
        return this.templateSaver;
    }

    @Override
    public ITemplate getTemplateByName(String name){
        for (ITemplate iTemplate : templateList) {
            if(iTemplate.getName().equals(name)){
                return iTemplate;
            }
        }
        return null;
    }

    @Override
    public void reloadTemplates() {
        templateList = getTemplateLoader().loadTemplates();
    }

}
