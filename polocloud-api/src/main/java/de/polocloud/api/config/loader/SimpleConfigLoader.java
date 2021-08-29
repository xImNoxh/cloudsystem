package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.util.PoloHelper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimpleConfigLoader implements IConfigLoader {

    @Override
    public <T> T load(Class<? extends IConfig> configClass, File file) {
        if (!file.exists()) {
            return (T) PoloHelper.getInstance(configClass);
        }
        T result = null;
        try {
            FileReader reader = new FileReader(file);
            result = (T) PoloHelper.GSON_INSTANCE.fromJson(reader, configClass);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
