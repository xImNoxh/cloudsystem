package de.polocloud.api.template.file;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.loading.ITemplateLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FileTemplateLoader implements ITemplateLoader {


    @Override
    public List<ITemplate> loadTemplates() {

        File baseDir = FileConstants.MASTER_TEMPLATE_INFO;

        if (!baseDir.exists()) {
            baseDir.mkdir();
            return new ArrayList<>();
        }

        List<ITemplate> templates = new ArrayList<>();

        for (File file : Objects.requireNonNull(baseDir.listFiles())) {
            try {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    templates.add(loadTemplate(file));
                }
            } catch (IOException e) {
                e.printStackTrace();
                PoloLogger.print(LogLevel.ERROR, "An exception was caught while loading a template! The template, with the file:" + file.getName() + " seems to be corrupt. Please check this template");
                PoloCloudAPI.getInstance().reportException(e);
            }
        }
        return templates;
    }

    public ITemplate loadTemplate(File file) throws IOException {
        return new JsonData(file).getAs(SimpleTemplate.class);
    }

}
