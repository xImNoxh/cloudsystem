package de.polocloud.bootstrap.template.file;

import com.google.gson.Gson;
import com.google.inject.Inject;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateLoader;
import de.polocloud.bootstrap.template.SimpleTemplate;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FileTemplateLoader implements ITemplateLoader {


    @Inject
    private Gson gson;

    @Override
    public Collection<ITemplate> loadTemplates() {

        File baseDir = new File("templates/");

        if (!baseDir.exists()) {
            baseDir.mkdir();
            return Collections.EMPTY_LIST;
        }

        List<ITemplate> templates = new ArrayList<>();

        for (File file : baseDir.listFiles()) {
            try {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    templates.add(loadTemplate(file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return templates;
    }

    public ITemplate loadTemplate(File file) throws IOException {
        FileReader reader = new FileReader(file);
        ITemplate template = gson.fromJson(reader, SimpleTemplate.class);
        reader.close();
        return template;
    }

}
