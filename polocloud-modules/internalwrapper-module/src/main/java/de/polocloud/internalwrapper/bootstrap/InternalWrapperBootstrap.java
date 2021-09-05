package de.polocloud.internalwrapper.bootstrap;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.internalwrapper.InternalWrapper;
import de.polocloud.internalwrapper.utils.config.WrapperConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class InternalWrapperBootstrap {

    public InternalWrapperBootstrap() {
    }

    /**
     * Checks for the temp-folder where dynamic-servers
     * are stored and deletes it
     * Then recreates it
     */
    public void checkAndDeleteTmpFolder() {
        File tmpFile = FileConstants.WRAPPER_DYNAMIC_SERVERS;
        if (tmpFile.exists()) {
            try {
                FileUtils.forceDelete(tmpFile);
            } catch (IOException exception) {
                exception.printStackTrace();
                PoloLogger.print(LogLevel.ERROR, "Unexpected error while deleting tmp Folder! Cloud may react abnormal!\n" +
                    "Please report this error.");
            }
        }
        File tempFilesDir = FileConstants.WRAPPER_TEMP_FILES;
        if (tempFilesDir.exists()) {
            try {
                FileUtils.forceDelete(tempFilesDir);
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Loads thw {@link WrapperConfig} for the Wrapper instance
     */
    public WrapperConfig loadWrapperConfig(WrapperBootstrap base) {
        File configFile = new File(base.getDataDirectory(), "wrapper.json");
        IConfigLoader configLoader = new SimpleConfigLoader();

        WrapperConfig wrapperConfig = configLoader.load(WrapperConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);

        return wrapperConfig;
    }


}
