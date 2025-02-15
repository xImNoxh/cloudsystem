package de.polocloud.modules.permission.pluginside.bungee;

import de.polocloud.modules.permission.PermsModule;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProxyBootstrap extends Plugin implements Listener {

    private PermsModule permissionModule;


    @Override
    public void onDisable() {
        this.permissionModule.shutdown();
    }

    @Override
    public void onEnable() {
        permissionModule = new PermsModule(new ModuleBootstrap());
        permissionModule.load();
        this.permissionModule.enable();
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        //Ignoring just checking if all ranks and perms are still valid
        PermissionPool.getInstance().loadPermissions(player.getUniqueId());

        //Just a check to call the PermissionCheckEvent and create a new user if non existent
        player.hasPermission(UUID.randomUUID().toString());

    }

    @EventHandler
    public void checkPerms(PermissionCheckEvent event) {
        String permission = event.getPermission();
        CommandSender sender = event.getSender();

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer)sender;

            IPermissionUser permissionUser = PermissionPool.getInstance().getCachedPermissionUser(player.getUniqueId());
            if (permissionUser == null) {

                Map<String, Long> groups = new HashMap<>();
                for (IPermissionGroup permissionGroup : PermissionPool.getInstance().getAllCachedPermissionGroups()) {
                    if (permissionGroup.isDefaultGroup()) {
                        groups.put(permissionGroup.getName(), -1L);
                    }
                }
                permissionUser = new SimplePermissionUser(player.getName(), player.getUniqueId(), groups, new ArrayList<>());
                PermissionPool.getInstance().createPermissionUser(permissionUser);
            }
            if (permissionUser.getName().equalsIgnoreCase("name_needs_to_be_set")) {
                ((SimplePermissionUser)permissionUser).setName(player.getName());
            }
            if (permissionUser.getUniqueId() == null) {
                ((SimplePermissionUser)permissionUser).setUniqueId(player.getUniqueId());
            }
            permissionUser.update();

            event.setHasPermission(permissionUser.hasPermission(permission));
        } else {
            event.setHasPermission(true);
        }
    }

}
