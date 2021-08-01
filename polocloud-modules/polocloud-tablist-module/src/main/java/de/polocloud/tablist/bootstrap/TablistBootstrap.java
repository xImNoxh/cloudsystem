package de.polocloud.tablist.bootstrap;

import de.polocloud.api.module.Module;
import de.polocloud.tablist.TablistModule;
import de.polocloud.tablist.scheduler.IntervalRunnable;

public class TablistBootstrap extends Module {

    @Override
    public void onLoad() {
        new TablistModule(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {
        IntervalRunnable intervalRunnable = TablistModule.getInstance().getIntervalRunnable();
        if (intervalRunnable != null) intervalRunnable.destroy();
    }
}
