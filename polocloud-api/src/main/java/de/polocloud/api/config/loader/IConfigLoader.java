package de.polocloud.api.config.loader;

import de.polocloud.api.config.IConfig;

import java.io.File;

public interface IConfigLoader {

    <T> T load(Class<? extends IConfig> config, File file);
    
}
