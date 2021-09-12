package de.polocloud.modules.proxy.api;

import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.modules.proxy.ProxyModule;
import de.polocloud.modules.proxy.api.motd.ProxyMotd;
import de.polocloud.modules.proxy.api.notify.NotifyConfig;
import de.polocloud.modules.proxy.api.tablist.ProxyTabList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class ProxyConfig implements IConfig {

    /**
     * The motd if the proxy is in maintenance
     */
    private ProxyMotd maintenanceMotd;

    /**
     * The motd if the proxy isn't in maintenance
     */
    private ProxyMotd onlineMotd;

    /**
     * The proxy tablist
     */
    private ProxyTabList tabList;

    /**
     * The notify config
     */
    private NotifyConfig notifyConfig;

    /**
     * All whitelisted players
     */
    private List<String> whiteListedPlayers;

    public ProxyConfig() {

        this.maintenanceMotd = new ProxyMotd(
            true,
            "§8» §cMaintenance",
            "§8»  §bPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o1.8 §8- §f§o1.16",
            "§8§l» §7This network is in §cmaintenance§8...",
            new String[]
                {
                    "§8§m---------------------",
                    "§8► §bPoloCloud §8┃ §7A modern CloudSystem",
                    "§8", "§8► §7Website §8» §fpolocloud.de",
                    "§8► §7Twitter §8» §f@PoloCloud",
                    "§8",
                    "§8§m---------------------"
                });

        this.onlineMotd = new ProxyMotd(
            true,
            null,
            "§8»  §bPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o1.8 §8- §f§o1.16",
            "§8§l» §7This network is §aonline§8...",
            new String[0]);

        this.notifyConfig = new NotifyConfig(true,
            "cloud.notify",
            "§7The service §3%service% §7is §estarting§8...",
            "§7The service §3%service% §7is now §aonline§8!",
            "§7The service §3%service% §7is §cstopping§8...",
            new ArrayList<>()
        );


        this.tabList = new ProxyTabList(
            true,
            60L,
            new String[]
                {
                    " \n     §b§lPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o%ONLINE_PLAYERS%§8/§f%MAX_PLAYERS% \n §7current server §8- §b%SERVICE% \n ",
                    " \n     §b§lPoloCloud §8▪§7▪ §7A modern cloudsystem §8» §f§o%ONLINE_PLAYERS%§8/§f%MAX_PLAYERS% \n §7current proxy §8- §b%PROXY% \n ",
                },
            new String[]
                {
                    " \n §7Join §bOUR §7discord §8» §bdc.polocloud.de \n §7Created by §8» §7HttpMarco§8, §7Max_DE, iPommes, Lystx \n "
                   // " \n §7See §bOUR §7website §8» §bpolocloud.de \n §7Version §8» §7%CLOUD_VERSION% \n "
                });
        this.whiteListedPlayers = new ArrayList<>();
    }

    /**
     * Updates this config
     */
    public void update() {

        ProxyModule.getProxyModule().setProxyConfig(this);
        ProxyModule.getProxyModule().getMessageChannel().sendMessage(this);
    }

    /**
     * Toggles receiving notify messages for a player with a given {@link UUID}
     *
     * @param uniqueId the uuid of the player
     */
    public boolean toggleNotify(UUID uniqueId) {
        boolean toggled;

        List<UUID> disabledMessages = this.notifyConfig.getDisabledMessages();

        if (disabledMessages.contains(uniqueId)) {
            disabledMessages.remove(uniqueId);
            toggled = false;
        } else {
            disabledMessages.add(uniqueId);
            toggled = true;
        }

        notifyConfig.setDisabledMessages(disabledMessages);

        return toggled;
    }

    /**
     * Adds a player to the whitelist
     *
     * @param name the name of the player
     */
    public void whitelistPlayer(String name) {
        if (!this.whiteListedPlayers.contains(name)) {
            this.whiteListedPlayers.add(name);
        }
        this.update();
    }

    /**
     * Removes a player from the whitelist
     *
     * @param name the name of the player
     */
    public void unWhitelistPlayer(String name) {
        this.whiteListedPlayers.remove(name);
        this.update();
    }

    /**
     * Gets the {@link ProxyMotd} based on a provided {@link IGameServer}
     * If the {@link de.polocloud.api.template.base.ITemplate} of the server
     * is in maintenance it will chose the maintenance motd otherwhise
     * it will return the online motd
     *
     * @param gameServer the server
     * @return chosen motd
     */
    public ProxyMotd getMotdBaseOnServer(IGameServer gameServer) {
        return gameServer.getTemplate().isMaintenance() ? this.maintenanceMotd : this.onlineMotd;
    }

}
