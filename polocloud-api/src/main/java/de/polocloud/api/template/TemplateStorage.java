package de.polocloud.api.template;

import de.polocloud.api.template.loading.ITemplateLoader;
import de.polocloud.api.template.loading.ITemplateSaver;
import de.polocloud.api.template.file.FileTemplateLoader;
import de.polocloud.api.template.file.FileTemplateSaver;
import de.polocloud.api.template.sql.SqlTemplateLoader;
import de.polocloud.api.template.sql.SqlTemplateSaver;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum TemplateStorage {

    FILE(new FileTemplateLoader(), new FileTemplateSaver()),
    SQL(new SqlTemplateLoader(), new SqlTemplateSaver());

    private final ITemplateLoader templateLoader;
    private final ITemplateSaver templateServer;

}
