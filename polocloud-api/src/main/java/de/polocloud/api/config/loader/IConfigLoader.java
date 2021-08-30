package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.saver.IConfigSaver;

import java.io.File;

public interface IConfigLoader {

    /**
     * Loads an {@link IConfig} object from a given typeClass
     *
     * @param config the class of the config object
     * @param file the file to load it from
     * @param <T> the generic-type
     * @return loaded config object
     */
    <T> T load(Class<? extends IConfig> config, File file);

    /**
     * Loads an {@link IConfig} object from a given typeClass
     * But automatically fallbacks to a provided {@link IConfigSaver} to save
     * the config if the file does not exist!
     *
     * @param config the class of the config object
     * @param file the file to load it from
     * @param <T> the generic-type
     * @return loaded config object
     */
    <T> T load(Class<? extends IConfig> config, IConfig defaultConfig, File file, IConfigSaver saver);
}
