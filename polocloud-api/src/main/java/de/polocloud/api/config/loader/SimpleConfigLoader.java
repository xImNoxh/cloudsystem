package de.polocloud.api.config.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.util.PoloUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimpleConfigLoader implements IConfigLoader {

    @Override
    public <T> T load(Class<? extends IConfig> configClass, File file) {
        if (!file.exists()) {
            return (T) PoloUtils.getInstance(configClass);
        }
        T result = null;
        try {
            FileReader reader = new FileReader(file);
            result = (T) PoloUtils.GSON_INSTANCE.fromJson(reader, configClass);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
