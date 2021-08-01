package de.polocloud.notify.bootstrap;

import de.polocloud.api.module.Module;
import de.polocloud.notify.NotifyModule;

public class NotifyBootstrap extends Module {

    @Override
    public void onLoad() {
        new NotifyModule(this);
    }

    @Override
    public boolean onReload() {
        return true;
    }

    @Override
    public void onShutdown() {

    }
}
