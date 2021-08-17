package de.polocloud.bootstrap.template.file;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateSaver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileTemplateSaver implements ITemplateSaver {

    @Inject
    private Gson gson;

    public void save(ITemplate template) {
        File templateFile = new File("templates/" + template.getName() + ".json");

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
        }

    }
}
