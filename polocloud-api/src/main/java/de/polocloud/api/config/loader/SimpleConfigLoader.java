package de.polocloud.api.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import de.polocloud.api.config.IConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimpleConfigLoader implements IConfigLoader {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Override
    public <T> T load(Class<? extends IConfig> configClass, File file) {
        if(!file.exists()){
            try {
                return (T) configClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        T result = null;
        try {
            FileReader reader = new FileReader(file);
            result = (T) gson.fromJson(reader, configClass);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
