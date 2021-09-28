package de.polocloud.wrapper.bootup;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
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

    /**
     * The PoloCloudClient for the PoloCloudUpdater and
     * the ExceptionReporterService
     */
    private final PoloCloudClient poloCloudClient;

    /**
     * Config for the updater
     */
    private WrapperLauncherConfig launcherConfig;

    public InternalWrapperBootstrap(Wrapper wrapper, boolean devMode, boolean ignoreUpdater, InetSocketAddress updaterAddress) {
        this.wrapper = wrapper;
        this.ignoreUpdater = ignoreUpdater;
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

        if(!apiJarFile.exists()){
            try {
                URL inputUrl = getClass().getResource("/" + FileConstants.CLOUD_API_NAME);
                FileUtils.copyURLToFile(inputUrl, apiJarFile);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String currentVersion;
        boolean forceUpdate;
        if (apiJarFile.exists()) {
            forceUpdate = launcherConfig.isForceUpdate();
            currentVersion = wrapper.getConfig().getApiVersion();
        } else {
            forceUpdate = true;
            currentVersion = "No Version found";
        }

        if(!launcherConfig.isUseUpdater()){
            return;
        }

        PoloCloudUpdater updater = new PoloCloudUpdater(this.devMode, currentVersion, "api", apiJarFile);

        if (forceUpdate) {
            if (this.devMode){
                PoloLogger.print(LogLevel.DEBUG, "Downloading latest api-development build...");
                if (updater.download()) {
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest api-development build!");
                } else {
                    PoloLogger.print(LogLevel.ERROR, "Couldn't download latest api-development build!");
                }
            }else{
                PoloLogger.print(LogLevel.DEBUG, "Force updated activated. Downloading the latest api-build");
                if(!apiJarFile.exists()){
                    PoloLogger.print(LogLevel.DEBUG, "If you wonder why the force update is activated, because no api version was found...");
                }
                if (updater.download()) {
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest api-build!");
                } else {
                    PoloLogger.print(LogLevel.ERROR, "Couldn't download latest api-build!");
                }
            }
        } else if (this.devMode) {
            PoloLogger.print(LogLevel.DEBUG, "Downloading latest development api-build...");
            if (updater.download()) {
                
                PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest api-development build!");
            } else {
                PoloLogger.print(LogLevel.ERROR, "Couldn't download latest api-development build!");
            }
        } else {
            PoloLogger.print(LogLevel.DEBUG, "Searching for regular PoloCloud-API updates...");
            if (updater.check()) {
                PoloLogger.print(LogLevel.DEBUG, "Found a api-update! (" + currentVersion + " -> " + updater.getFetchedVersion() + " (Upload date: " + updater.getLastUpdate() + "))");
                PoloLogger.print(LogLevel.DEBUG, "Downloading...");
                if (updater.download()) {
                    wrapper.getConfig().setApiVersion(updater.getFetchedVersion());
                    new SimpleConfigSaver().save(wrapper.getConfig(), FileConstants.WRAPPER_CONFIG_FILE);
                    PoloLogger.print(LogLevel.DEBUG, "Successfully downloaded latest api-version! (" + updater.getFetchedVersion() + ")");
                } else {
                    PoloLogger.print(LogLevel.DEBUG, "Couldn't download latest api-version!");
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
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Wrapper.getInstance().reportException(e));
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
        this.launcherConfig = configLoader.load(WrapperLauncherConfig.class, new File("launcher.json"));

        IConfigSaver configSaver = new SimpleConfigSaver();
        configSaver.save(wrapperConfig, configFile);
        configSaver.save(this.launcherConfig, new File("launcher.json"));

        return wrapperConfig;
    }


}
