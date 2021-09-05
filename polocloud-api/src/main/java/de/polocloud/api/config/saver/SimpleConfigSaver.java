package de.polocloud.api.config.saver;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.util.gson.PoloHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleConfigSaver implements IConfigSaver {

    @Override
    public void save(IConfig config, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            PoloHelper.GSON_INSTANCE.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
