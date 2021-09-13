package de.polocloud.api.config.saver;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.JsonData;

import java.io.File;
import java.io.IOException;

public class SimpleConfigSaver implements IConfigSaver {

    @Override
    public void save(IConfig config, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            new JsonData(config).save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
