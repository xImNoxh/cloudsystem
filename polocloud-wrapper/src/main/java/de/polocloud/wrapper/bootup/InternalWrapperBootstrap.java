package de.polocloud.wrapper.bootup;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URL;

public class InternalWrapperBootstrap {

    /**
     * The wrapper instance
     */
    private final Wrapper wrapper;

    /**
     * If in developer mode
     */
    private final boolean devMode;

    /**
     * If updater should be ignored
     */
    private final boolean ignoreUpdater;

    public InternalWrapperBootstrap(Wrapper wrapper, boolean devMode, boolean ignoreUpdater) {
        this.wrapper = wrapper;
        this.ignoreUpdater = ignoreUpdater;
        this.devMode = devMode;
    }

    /**
     * Checks if the CloudAPI file exists
     * Otherwise it will download it from the given server
     * If the Updater-address is right and provided
     */
    public void checkPoloCloudAPI(){
        File apiJarFile = FileConstants.WRAPPER_CLOUD_API;

        if (!apiJarFile.getParentFile().exists()) {
            apiJarFile.getParentFile().mkdirs();
        }

        try {
            URL inputUrl = getClass().getResource("/" + FileConstants.CLOUD_API_NAME);
            FileUtils.copyURLToFile(inputUrl, apiJarFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentVersion;
        boolean forceUpdate;
        if (apiJarFile.exists()) {
            forceUpdate = false;
            currentVersion = wrapper.getConfig().getApiVersion();
        } else {

            forceUpdate = true;
            currentVersion = "First download";
        }
        if (ignoreUpdater) {
            return;
        }

    }

    /**
     * Registers the exception handler
     * to automatically report errors to the
     * PoloCloud-Server
     */
    public void registerUncaughtExceptionListener(){
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            throwable.printStackTrace();
            PoloLogger.print(LogLevel.ERROR, "This is an error. Please report this error at our discord.");
        });
    }

    /**
     * Checks for the temp-folder where dynamic-servers
     * are stored and deletes it
     * Then recreates it
     */
    public void checkAndDeleteTmpFolder() {
        File tmpFile = FileConstants.WRAPPER_DYNAMIC_SERVERS;
        if (!tmpFile.exists()) {
            tmpFile.mkdirs();
        }
    }

    /**
     * Loads thw {@link WrapperConfig} for the Wrapper instance
     */
    public WrapperConfig loadWrapperConfig() {

        File configFile = FileConstants.WRAPPER_CONFIG_FILE;
        IConfigLoader configLoader = new SimpleConfigLoader();

        WrapperConfig wrapperConfig = configLoader.load(WrapperConfig.class, configFile);

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);

        return wrapperConfig;
    }


}
