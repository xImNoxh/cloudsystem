package de.polocloud.modules.permission.pluginside;

import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import net.md_5.bungee.api.plugin.Plugin;

public class ProxyBootstrap extends Plugin {

    private PermissionModule permissionModule;

    @Override
    public void onLoad() {
        permissionModule = new PermissionModule(new ModuleBootstrap());
        permissionModule.load();
    }

    @Override
    public void onDisable() {
        this.permissionModule.shutdown();
    }

    @Override
    public void onEnable() {
        this.permissionModule.enable();
    }
}
