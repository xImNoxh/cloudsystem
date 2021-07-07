package de.polocloud.bootstrap.template;

import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.api.template.ITemplateSaver;
import de.polocloud.bootstrap.template.file.FileTemplateLoader;
import de.polocloud.bootstrap.template.file.FileTemplateSaver;
import de.polocloud.bootstrap.template.sql.SqlTemplateLoader;
import de.polocloud.bootstrap.template.sql.SqlTemplateSaver;

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
