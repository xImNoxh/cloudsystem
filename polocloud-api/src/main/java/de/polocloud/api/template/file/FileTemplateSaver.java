package de.polocloud.api.template.file;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.loading.ITemplateSaver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileTemplateSaver implements ITemplateSaver {

    @Inject
    private Gson gson;

    public void save(ITemplate template) {
        File templateFile = new File(FileConstants.MASTER_TEMPLATE_INFO ,template.getName() + ".json");

        if (!templateFile.exists()) {
            try {
                templateFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(templateFile);

            gson.toJson(template, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
