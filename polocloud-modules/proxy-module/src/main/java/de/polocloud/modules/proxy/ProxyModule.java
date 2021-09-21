package de.polocloud.modules.proxy;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.common.IReloadable;
import de.polocloud.modules.proxy.api.ProxyConfig;
import de.polocloud.modules.proxy.api.motd.MotdService;
import de.polocloud.modules.proxy.api.notify.NotifyService;
import de.polocloud.modules.proxy.api.tablist.TablistService;
import de.polocloud.modules.proxy.cloudside.listener.NotifyListener;
import de.polocloud.modules.proxy.cloudside.listener.ServerConnectListener;
import de.polocloud.modules.proxy.cloudside.listener.ServerMotdListener;
import de.polocloud.modules.proxy.cloudside.listener.TabListListener;
import de.polocloud.modules.proxy.pluginside.global.command.ProxyCommand;
import de.polocloud.modules.proxy.pluginside.global.listener.WhitelistListener;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProxyModule {

    /**
     * The instance of this module
     */
    @Getter
    private static ProxyModule proxyModule;

    /**
     * All reloadable sub-modules
     */
    private final List<IReloadable> reloaded;

    /**
     * The configFile
     */
    private final File configFile;

    /**
     * The motd service for managing motd
     */
    private MotdService motdService;

    /**
     * The notify service
     */
    private NotifyService notifyService;

    /**
     * The tablist service
     */
    private TablistService tablistService;

    /**
     * The proxy config object
     */
    private ProxyConfig proxyConfig;

    /**
     * The message channel for the config
     */
    private final IMessageChannel<ProxyConfig> messageChannel;

    /**
     * Initializes this module with a given {@link CloudModule} instance
     *
     * @param module the instance
     */
    public ProxyModule(CloudModule module) {
        proxyModule = this;

        this.reloaded = new ArrayList<>();
        this.proxyConfig = new ProxyConfig();
        this.notifyService = new NotifyService();
        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(ProxyConfig.class, "proxy-module");

        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            //Module side loading
            this.configFile = new File(module.getDataDirectory(), "config.json");
            if (!this.configFile.exists()) {
                PoloCloudAPI.getInstance().getConfigSaver().save(proxyConfig, this.configFile);
            } else {
                this.proxyConfig = PoloCloudAPI.getInstance().getConfigLoader().load(ProxyConfig.class, this.configFile);
            }

            PoloCloudAPI.getInstance().getEventManager().registerListener(new ServerConnectListener());
            PoloCloudAPI.getInstance().getEventManager().registerListener(new ServerMotdListener());
            PoloCloudAPI.getInstance().getEventManager().registerListener(new TabListListener());
            PoloCloudAPI.getInstance().getEventManager().registerListener(new NotifyListener());
        } else {
            //Plugin side loading
            this.configFile = null;
        }

        this.messageChannel.registerListener((wrappedObject, startTime) -> {
            ProxyConfig proxyConfig = wrappedObject.unwrap(ProxyConfig.class);
            if (proxyConfig == null) {
                PoloLogger.print(LogLevel.ERROR, "§cProxy-Module received §enull ProxyConfig§c!");
                return;
            }
            this.proxyConfig = proxyConfig;
            if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                PoloCloudAPI.getInstance().getConfigSaver().save(proxyConfig, configFile);
                this.messageChannel.sendMessage(proxyConfig);
            }
        });
    }

    /**
     * Enables this module
     */
    public void enable() {
        reloaded.add(this.motdService = new MotdService());
        reloaded.add(this.tablistService = new TablistService());

        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            PoloCloudAPI.getInstance().getCommandManager().registerCommand(new ProxyCommand());
            PoloCloudAPI.getInstance().getEventManager().registerListener(new WhitelistListener());
        }
    }

    /**
     * Reloads this module
     */
    public void reload(){
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            this.proxyConfig = PoloCloudAPI.getInstance().getConfigLoader().load(ProxyConfig.class, this.configFile);
            this.reloaded.forEach(IReloadable::reload);
        }
        this.messageChannel.sendMessage(this.proxyConfig);
    }

    /**
     * Shuts down this module
     */
    public void shutdown() {

    }

}
