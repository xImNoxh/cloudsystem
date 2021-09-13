package de.polocloud.api.database.other.files;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.database.other.IDatabaseTemplateLoader;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileDatabaseTemplateLoader implements IDatabaseTemplateLoader<FileDatabase> {

    @Override
    public List<ITemplate> loadTemplates() {

        File baseDir = FileConstants.MASTER_TEMPLATE_INFO;

        if (!baseDir.exists()) {
            baseDir.mkdir();
            return new ArrayList<>();
        }

        List<ITemplate> templates = new ArrayList<>();

        for (File file : Objects.requireNonNull(baseDir.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                templates.add(new JsonData(file).getAs(SimpleTemplate.class));
            }
        }
        return templates;
    }

    @Override
    public void insertTemplate(ITemplate template) {
        File file = new File(FileConstants.MASTER_TEMPLATE_INFO, template.getName() + ".json");
        new JsonData(file).append(template).save();
    }

    @Override
    public void deleteTemplate(String name) {
        File file = new File(FileConstants.MASTER_TEMPLATE_INFO, name + ".json");
        file.delete();
    }

    @Override
    public void saveTemplates() {
        for (ITemplate template : PoloCloudAPI.getInstance().getTemplateManager().getTemplates()) {
            this.insertTemplate(template);
        }
    }
}
