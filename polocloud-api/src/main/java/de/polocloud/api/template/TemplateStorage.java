package de.polocloud.api.template;

import de.polocloud.api.template.loading.ITemplateLoader;
import de.polocloud.api.template.loading.ITemplateSaver;
import de.polocloud.api.template.file.FileTemplateLoader;
import de.polocloud.api.template.file.FileTemplateSaver;
import de.polocloud.api.template.sql.SqlTemplateLoader;
import de.polocloud.api.template.sql.SqlTemplateSaver;

public enum TemplateStorage {

    FILE(FileTemplateLoader.class, FileTemplateSaver.class),
    SQL(SqlTemplateLoader.class, SqlTemplateSaver.class);

    private final Class<? extends ITemplateLoader> templateLoader;
    private final Class<? extends ITemplateSaver> templateServer;

    TemplateStorage(Class<? extends ITemplateLoader> templateLoader, Class<? extends ITemplateSaver> templateServer) {
        this.templateLoader = templateLoader;
        this.templateServer = templateServer;
    }

    public Class<? extends ITemplateLoader> getTemplateLoader() {
        return templateLoader;
    }

    public Class<? extends ITemplateSaver> getTemplateServer() {
        return templateServer;
    }
}
