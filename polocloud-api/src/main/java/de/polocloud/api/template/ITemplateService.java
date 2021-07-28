package de.polocloud.api.template;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface ITemplateService {

    ITemplateLoader getTemplateLoader();

    ITemplateSaver getTemplateSaver();

    ITemplate getTemplateByName(String name);

    CompletableFuture<Collection<ITemplate>> getLoadedTemplates();

    void reloadTemplates();

}
