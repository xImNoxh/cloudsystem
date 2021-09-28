package de.polocloud.api.config.saver;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

import java.io.File;
import java.io.IOException;

public class SimpleConfigSaver implements IConfigSaver {

    @Override
    public void save(IConfig config, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            new JsonData(config).save(file);
        } catch (IOException e) {
            e.printStackTrace();
            PoloLogger.print(LogLevel.ERROR, "Failed to save a config (" + file.getPath() + "). The file or the Config-Object could be corrupt!");
            PoloCloudAPI.getInstance().reportException(e);
        }
    }
}
