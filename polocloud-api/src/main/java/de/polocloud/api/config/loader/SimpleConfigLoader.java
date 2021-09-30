package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.util.PoloHelper;

import java.io.File;

public class SimpleConfigLoader implements IConfigLoader {

    @Override
    public <T> T load(Class<? extends IConfig> configClass, File file) {
        if (!file.exists()) {
            return (T) PoloHelper.getInstance(configClass);
        }
        return (T) new JsonData(file).getAs(configClass);
    }

    @Override
    public <T> T load(Class<? extends IConfig> configClass, IConfig defaultConfig, File file, IConfigSaver saver) {
        if (!file.exists()) {
            saver.save(defaultConfig, file);
            return (T) defaultConfig;
        }
        return (T) new JsonData(file).getAs(configClass);
    }
}
