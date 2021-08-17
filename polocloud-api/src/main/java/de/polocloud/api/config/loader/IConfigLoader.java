package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;

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
    
}
