package de.polocloud.wrapper.bootup;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

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
     * The PoloCloudClient for the PoloCloudUpdater and
     * the ExceptionReporterService
     */
    private final PoloCloudClient poloCloudClient;

    public InternalWrapperBootstrap(Wrapper wrapper, boolean devMode, InetSocketAddress updaterAddress) {
        this.wrapper = wrapper;
        this.devMode = devMode;
        this.poloCloudClient = new PoloCloudClient(updaterAddress.getAddress().getHostAddress(), updaterAddress.getPort());
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

        String currentVersion;
        boolean forceUpdate;
        if (apiJarFile.exists()) {
            forceUpdate = false;
            currentVersion = wrapper.getConfig().getApiVersion();
        } else {
            forceUpdate = true;
            currentVersion = "First download";
        }
        PoloCloudUpdater updater = new PoloCloudUpdater(this.devMode, currentVersion, "api", apiJarFile);

        if (forceUpdate) {
            if (this.devMode){
                PoloLogger.print(LogLevel.DEBUG, "Downloading latest development build...");
                if (updater.download()) {
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest development build!");
                } else {
                    PoloLogger.print(LogLevel.ERROR, "Couldn't download latest development build!");
                }
            }else{
                PoloLogger.print(LogLevel.DEBUG, "Force update was due to no version of the PoloCloud-API found activated. Downloading latest build...");
                if (updater.download()) {
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest build!");
                } else {
                    PoloLogger.print(LogLevel.ERROR, "Couldn't download latest build!");
                }
            }
        }else if(this.devMode){
            PoloLogger.print(LogLevel.DEBUG, "Downloading latest development build...");
            if (updater.download()) {
                
                PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest development build!");
            } else {
                PoloLogger.print(LogLevel.ERROR, "Couldn't download latest development build!");
            }
        }else{
            PoloLogger.print(LogLevel.DEBUG, "Searching for regular PoloCloud-API updates...");
            if (updater.check()) {
                PoloLogger.print(LogLevel.DEBUG, "Found a update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                PoloLogger.print(LogLevel.DEBUG, "downloading...");
                if (updater.download()) {
                    wrapper.getConfig().setApiVersion(updater.getFetchedVersion());
                    new SimpleConfigSaver().save(wrapper.getConfig(), FileConstants.WRAPPER_CONFIG_FILE);
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest version! (" + updater.getFetchedVersion() + ")");
                } else {
                    PoloLogger.print(LogLevel.DEBUG, "Couldn't download latest version!");
                }
            } else {
                PoloLogger.print(LogLevel.DEBUG, "You are running the latest version of the PoloCloud-API! (" + currentVersion + ")");
            }
        }

    }

    /**
     * Registers the exception handler
     * to automatically report errors to the
     * PoloCloud-Server
     */
    public void registerUncaughtExceptionListener(){
        String currentVersion = new JsonData(FileConstants.LAUNCHER_FILE).fallback("N/A").getString("version");
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> this.poloCloudClient.getExceptionReportService().reportException(throwable, "wrapper", currentVersion));
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
