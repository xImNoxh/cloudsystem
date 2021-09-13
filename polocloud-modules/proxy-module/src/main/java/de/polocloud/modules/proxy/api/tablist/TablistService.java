package de.polocloud.modules.proxy.api.tablist;

import com.google.common.collect.Lists;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.common.IReloadable;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.SchedulerFuture;
import de.polocloud.modules.proxy.ProxyModule;

public class TablistService implements IReloadable {

    /**
     * The last header index of the {@link ProxyTabList#getHeaders()}
     */
    private int lastHeader;

    /**
     * The last footer index of the {@link ProxyTabList#getFooters()}
     */
    private int lastFooter;

    /**
     * The schedulerfuture of the updater
     */
    private SchedulerFuture schedulerFuture;

    public TablistService() {
        this.lastHeader = -1;
        this.lastFooter = -1;

        this.startUpdateTask();
    }

    /**
     * Starts the updating task for the tablist
     */
    private void startUpdateTask() {
        ProxyTabList tabList = ProxyModule.getProxyModule().getProxyConfig().getTabList();
        this.schedulerFuture = Scheduler.runtimeScheduler().schedule(this::updateTabList, tabList.getUpdateInterval(), tabList.getUpdateInterval());
    }

    /**
     * Updates the current task
     * (Cancels it if it exists and re-runs it)
     */
    private void updateTask() {
        if (schedulerFuture != null) {
            Scheduler.runtimeScheduler().cancelTask(schedulerFuture);
        }

        this.startUpdateTask();
    }

    /**
     * Updates the tablist for only one {@link ICloudPlayer}
     *
     * @param cloudPlayer the player
     */
    public void updateTabList(ICloudPlayer cloudPlayer) {
        ProxyTabList tabList = ProxyModule.getProxyModule().getProxyConfig().getTabList();

        if (!tabList.isEnabled()) {
            return;
        }

        String header;
        String footer;

        try {
            header = tabList.getHeaders()[lastHeader];
        } catch (IndexOutOfBoundsException e) {
            header = tabList.getHeaders()[0];
        }

        try {
            footer = tabList.getFooters()[lastFooter];
        } catch (IndexOutOfBoundsException e) {
            footer = tabList.getFooters()[0];
        }

        cloudPlayer.sendTabList(
            formatTabComponent(header, cloudPlayer),
            formatTabComponent(footer, cloudPlayer)
        );
    }

    /**
     * Updates the current tablist if the update interval
     * is not 0 or lower (so it should not be animated)
     */
    public void updateTabListIfRequired() {
        ProxyTabList tabList = ProxyModule.getProxyModule().getProxyConfig().getTabList();
        if (tabList.getUpdateInterval() > 0) {
            this.updateTask();
        }
    }

    /**
     * Updates the {@link ProxyTabList} for every online {@link ICloudPlayer}
     */
    public void updateTabList() {
        ProxyTabList tabList = ProxyModule.getProxyModule().getProxyConfig().getTabList();

        if (!tabList.isEnabled()) {
            return;
        }

        String header;
        String footer;

        try {
            header = tabList.getHeaders()[lastHeader];
            lastHeader++;
        } catch (IndexOutOfBoundsException e) {
            lastHeader = 0;
            header = tabList.getHeaders()[0];
        }

        try {
            footer = tabList.getFooters()[lastFooter];
            lastFooter++;
        } catch (IndexOutOfBoundsException e) {
            lastFooter = 0;
            footer = tabList.getFooters()[0];
        }

        for (ICloudPlayer cloudPlayer : Lists.newArrayList(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached())) {
            cloudPlayer.sendTabList(
                formatTabComponent(header, cloudPlayer),
                formatTabComponent(footer, cloudPlayer)
            );
        }
    }

    /**
     * Replaces all TabList-components for a given {@link ICloudPlayer}
     *
     * @param value the input line
     * @param cloudPlayer the player
     * @return replaced input
     */
    private String formatTabComponent(String value, ICloudPlayer cloudPlayer) {

        if (cloudPlayer == null) {
            return value;
        }

        //Proxy and server
        value = value.replaceAll("%PROXY%", (cloudPlayer.getProxyServer() == null ? "Loading..." : cloudPlayer.getProxyServer().getName()));
        value = value.replaceAll("%SERVICE%", (cloudPlayer.getMinecraftServer() == null ? "Loading..." : cloudPlayer.getMinecraftServer().getName()));

        //Player values
        value = value.replaceAll("%MAX_PLAYERS%", String.valueOf(cloudPlayer.getProxyServer() == null ? 0 : cloudPlayer.getProxyServer().getMaxPlayers()));
        value = value.replaceAll("%ONLINE_PLAYERS%", String.valueOf(PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size()));

        //Other values
        value = value.replaceAll("%CLOUD_VERSION%", PoloCloudAPI.getInstance().getVersion().version());
        value = value.replaceAll("%CLOUD_DISCORD%", PoloCloudAPI.getInstance().getVersion().discord());

        //Translating colorCodes
        value = value.replaceAll("&", "§");

        return value;
    }

    @Override
    public void reload() {
        this.updateTask();
    }
}
