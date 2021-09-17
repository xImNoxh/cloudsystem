package de.polocloud.api.template.file;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.loading.ITemplateSaver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileTemplateSaver implements ITemplateSaver {

    public void save(ITemplate template) {
        File templateFile = new File(FileConstants.MASTER_TEMPLATE_INFO ,template.getName() + ".json");

        if (!templateFile.exists()) {
            try {
                templateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonData jsonData = new JsonData(templateFile);

        jsonData.append(template);
        jsonData.save();
    }
}
