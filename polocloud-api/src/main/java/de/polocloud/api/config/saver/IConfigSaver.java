package de.polocloud.api.config.saver;

import de.polocloud.api.config.IConfig;

import java.io.File;

public interface IConfigSaver {

    /**
     * Saves an {@link IConfig} object to a {@link File}
     *
     * @param config the config object
     * @param file the file to save it to
     */
    void save(IConfig config, File file);

}
