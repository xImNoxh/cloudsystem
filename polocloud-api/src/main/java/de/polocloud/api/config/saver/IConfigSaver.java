package de.polocloud.api.config.saver;

import de.polocloud.api.config.IConfig;

import java.io.File;

public interface IConfigSaver {

    void save(IConfig config, File file);

}
