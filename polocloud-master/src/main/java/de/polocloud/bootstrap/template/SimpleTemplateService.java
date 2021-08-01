package de.polocloud.bootstrap.template;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.api.template.ITemplateService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SimpleTemplateService implements ITemplateService {


    private ITemplateLoader templateLoader;
    private ITemplateSaver templateSaver;

    private Collection<ITemplate> templateList = new ArrayList<>();

    public SimpleTemplateService() {

    }

    public void load(CloudAPI cloudAPI, TemplateStorage templateStorage){
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
    public CompletableFuture<ITemplate> getTemplateByName(String name){
        return CompletableFuture.completedFuture(templateList.stream().filter(key -> key.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public CompletableFuture<Collection<ITemplate>> getLoadedTemplates() {
        return CompletableFuture.completedFuture(this.templateList);
    }

    @Override
    public void reloadTemplates() {
        templateList = getTemplateLoader().loadTemplates();
    }

}
