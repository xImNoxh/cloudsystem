package de.polocloud.proxy.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.proxy.config.motd.MotdLists;
import de.polocloud.proxy.config.slots.SlotSync;
import de.polocloud.proxy.config.whitelist.Whitelist;

public class ProxyConfig implements IConfig {

    private boolean use = true;
    private MotdLists motds = new MotdLists();
    private SlotSync slotSync = new SlotSync();
    private Whitelist whitelist = new Whitelist();

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public MotdLists getMotds() {
        return motds;
    }

    public void setMotds(MotdLists motds) {
        this.motds = motds;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(Whitelist whitelist) {
        this.whitelist = whitelist;
    }
}
