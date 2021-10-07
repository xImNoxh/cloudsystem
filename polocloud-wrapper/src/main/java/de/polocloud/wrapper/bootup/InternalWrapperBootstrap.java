package de.polocloud.wrapper.bootup;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import de.polocloud.wrapper.impl.config.launcher.WrapperLauncherConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;

public class InternalWrapperBootstrap {


    /**
     * Registers the exception handler
     * to automatically report errors to the
     * PoloCloud-Server
     */
    public void registerUncaughtExceptionListener(){
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Wrapper.getInstance().reportException(e));
    }

    /**
     * Checks for the temp-folder where dynamic-servers
     * are stored and deletes it
     * Then recreates it
     */
    public void checkAndDeleteTmpFolder() {
        File tmpFile = FileConstants.WRAPPER_DYNAMIC_SERVERS;
        PoloHelper.deleteFolder(tmpFile);
        tmpFile.mkdirs();
    }

    /**
     * Checks for the patcher-folder where downloaded
     * serverfiles will be patched
     */

    public void checkAndDeletePatchFolder() {
        File patcherFolder = FileConstants.WRAPPER_PATCHER_FOLDER;
        PoloHelper.deleteFolder(patcherFolder);
        patcherFolder.mkdirs();
    }

    /**
     * Loads thw {@link WrapperConfig} for the Wrapper instance
     */
    public WrapperConfig loadWrapperConfig() {

        File configFile = FileConstants.WRAPPER_CONFIG_FILE;
        IConfigLoader configLoader = new SimpleConfigLoader();

        WrapperConfig wrapperConfig = configLoader.load(WrapperConfig.class, configFile);
        WrapperLauncherConfig launcherConfig = configLoader.load(WrapperLauncherConfig.class, new File("launcher.json"));

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);
        configSaver.save(launcherConfig, new File("launcher.json"));

        return wrapperConfig;
    }

}
