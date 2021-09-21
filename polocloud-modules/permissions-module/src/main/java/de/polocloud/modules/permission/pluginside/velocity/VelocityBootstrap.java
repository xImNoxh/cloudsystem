package de.polocloud.modules.permission.pluginside.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.permission.PermissionSubject;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import de.polocloud.modules.permission.PermsModule;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;
import lombok.Getter;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Plugin(
    id = "perms",
    name = "PoloCloud-Permissions",
    version = "1.0.0",
    description = "This is the perms communication between cloud and proxy",
    authors = "Lystx",
    dependencies = @Dependency(id = "bridge"),
    url = "https://polocloud.de"
) @Getter
public class VelocityBootstrap {

    private final ProxyServer server;
    private final Logger logger;
    private final PermsModule permissionModule;

    @Getter
    private static VelocityBootstrap instance;

    @Inject
    public VelocityBootstrap(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        permissionModule = new PermsModule(new ModuleBootstrap());
        permissionModule.load();
    }

    @Subscribe
    public void handle(ProxyInitializeEvent event) {
        this.permissionModule.enable();
    }

    @Subscribe
    public void handle(ProxyShutdownEvent event) {
        this.permissionModule.shutdown();
    }

    @Subscribe
    public void handle(com.velocitypowered.api.event.permission.PermissionsSetupEvent event) {
        PermissionSubject subject = event.getSubject();

        if (subject instanceof Player) {
            Player player = (Player)subject;
            PermissionPool pool = PermissionPool.getInstance();

            //Ignoring just checking if all ranks and perms are still valid
            pool.loadPermissions(player.getUniqueId());

            IPermissionUser permissionUser = pool.getCachedPermissionUser(player.getUniqueId());

            if (permissionUser == null) {

                Map<String, Long> groups = new HashMap<>();
                for (IPermissionGroup permissionGroup : PermissionPool.getInstance().getAllCachedPermissionGroups()) {
                    if (permissionGroup.isDefaultGroup()) {
                        groups.put(permissionGroup.getName(), -1L);
                    }
                }
                permissionUser = new SimplePermissionUser(player.getUsername(), player.getUniqueId(), groups, new ArrayList<>());
                PermissionPool.getInstance().createPermissionUser(permissionUser);
            }
            if (permissionUser.getName().equalsIgnoreCase("name_needs_to_be_set")) {
                ((SimplePermissionUser)permissionUser).setName(player.getUsername());
            }
            if (permissionUser.getUniqueId() == null) {
                ((SimplePermissionUser)permissionUser).setUniqueId(player.getUniqueId());
            }
            permissionUser.update();

            IPermissionUser finalPermissionUser = permissionUser;
            event.setProvider(subject1 -> s -> {

                boolean b = finalPermissionUser.hasPermission(s);
                return Tristate.fromBoolean(b);
            });
        }
    }
}
