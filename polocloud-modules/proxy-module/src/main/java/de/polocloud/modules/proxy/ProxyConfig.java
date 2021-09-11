package de.polocloud.modules.proxy;

import de.polocloud.api.config.IConfig;
import de.polocloud.modules.proxy.motd.config.ProxyMotd;
import de.polocloud.modules.proxy.motd.config.ProxyMotdSettings;
import de.polocloud.modules.proxy.notify.config.NotifyConfig;
import de.polocloud.modules.proxy.tablist.config.TablistConfig;
import de.polocloud.modules.proxy.whitelist.config.Whitelist;

public class ProxyConfig implements IConfig {

    private ProxyMotdSettings proxyMotdSettings;
    private NotifyConfig notifyConfig;
    private TablistConfig tablistConfig;
    private Whitelist whitelist;

    public ProxyConfig() {
        this.proxyMotdSettings = new ProxyMotdSettings(
            new ProxyMotd("§8»  §b§lPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o1.8 §8- §f§o1.16", "§8§l» §7This network is in §c§lmaintenance§8...","§8» §cMaintenance", new String[]{"§8§m---------------------", "§8► §bPoloCloud §8┃ §7A modern CloudSystem", "§8", "§8► §7Website §8» §fpolocloud.de", "§8► §7Twitter §8» §f@PoloCloud", "§8", "§8§m---------------------"}),
            new ProxyMotd("§8»  §b§lPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o1.8 §8- §f§o1.16","§8§l» §7This network is §a§lonline§8...",null, new String[0])
        );

        this.notifyConfig = new NotifyConfig("cloud.notify",
            "§7The service §e%service% §7is now §6§lstarted§8...",
            "§7The service §e%service% §7is now §a§lonline§8.",
            "§7The service §e%service% §7will §c§lstopped§8.");

        this.tablistConfig = new TablistConfig(" \n     §b§lPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o%ONLINE_PLAYERS%§8/§f%MAX_PLAYERS% " +
            "\n §7current server §8- §b%SERVICE% \n "," \n §7Join §b§nOUR §7discord §8» §bdc.polocloud.de \n §7Created by §8» §7HttpMarco§8, §7Max_DE, iPommes, Lystx \n ");

        this.whitelist = new Whitelist();
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public TablistConfig getTablistConfig() {
        return tablistConfig;
    }

    public ProxyMotdSettings getProxyMotdSettings() {
        return proxyMotdSettings;
    }

    public NotifyConfig getNotifyConfig() {
        return notifyConfig;
    }

}
