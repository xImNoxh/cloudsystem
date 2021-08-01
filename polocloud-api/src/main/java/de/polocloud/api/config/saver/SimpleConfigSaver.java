package de.polocloud.api.config.saver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.config.IConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SimpleConfigSaver implements IConfigSaver {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void save(IConfig config, File file) {
        try {
            FileWriter writer = new FileWriter(file);
            gson.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
