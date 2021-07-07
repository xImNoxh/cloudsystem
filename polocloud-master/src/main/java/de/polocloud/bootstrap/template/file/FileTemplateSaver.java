package de.polocloud.bootstrap.template.file;

import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateSaver;

public class FileTemplateSaver implements ITemplateSaver {
    @Override
    public void save(ITemplate template) {
        System.out.println("saving template to file");
    }
}
