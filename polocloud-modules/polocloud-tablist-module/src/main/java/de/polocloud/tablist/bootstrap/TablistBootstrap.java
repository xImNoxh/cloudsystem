package de.polocloud.tablist.bootstrap;

import de.polocloud.api.module.Module;
import de.polocloud.tablist.TablistModule;

public class TablistBootstrap extends Module {

    @Override
    public void onLoad() {
        new TablistModule(this);
    }

    @Override
    public void onShutdown() {

    }
}
