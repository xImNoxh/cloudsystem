package de.polocloud.modules.permission.pluginside;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.gson.PoloHelper;
import de.polocloud.modules.permission.PermsModule;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

public class SpigotBootstrap extends JavaPlugin implements Listener {

    private PermsModule permissionModule;

    @Override
    public void onLoad() {

        permissionModule = new PermsModule(new ModuleBootstrap());
        permissionModule.load();
    }

    @Override
    public void onEnable() {
        this.permissionModule.enable();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        this.permissionModule.shutdown();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            Class<?> clazz = PoloHelper.getCraftBukkitClass("entity.CraftHumanEntity", Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
            Field field = clazz.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, new SpigotPermissible(player, this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Getter
    public static class SpigotPermissible extends PermissibleBase {

        /**
         * The player for this base
         */
        private final Player player;

        /**
         * The failed tries
         */
        private int tries;

        /**
         * The cached perms
         */
        private Map<String, PermissionAttachmentInfo> perms;

        /**
         * The plugin instance
         */
        private final JavaPlugin pluginInstance;

        public SpigotPermissible(Player player, JavaPlugin pluginInstance) {
            super(player);
            this.pluginInstance = pluginInstance;
            this.perms = new HashMap<>();
            this.player = player;
            this.tries = 0;

            player.setOp(false);
            this.recalculatePermissions();
        }

        @Override
        public boolean isOp() {
            return this.hasPermission("*");
        }

        @Override
        public boolean isPermissionSet(String name) {
            return this.hasPermission(name);
        }

        @Override
        public boolean isPermissionSet(Permission perm) {
            return this.hasPermission(perm.getName());
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return new HashSet<>(perms.values());
        }

        @Override
        public boolean hasPermission(Permission perm) {
            return this.hasPermission(perm.getName());
        }

        @Override
        public boolean hasPermission(String inName) {

            IPermissionUser cachedPermissionUser = PermissionPool.getInstance().getCachedPermissionUser(this.player.getUniqueId());
            if (cachedPermissionUser == null) {
                Map<String, Long> groups = new HashMap<>();
                for (IPermissionGroup permissionGroup : PermissionPool.getInstance().getAllCachedPermissionGroups()) {
                    if (permissionGroup.isDefaultGroup()) {
                        groups.put(permissionGroup.getName(), -1L);
                    }
                }
                cachedPermissionUser = new SimplePermissionUser(player.getName(), player.getUniqueId(), groups, new ArrayList<>());
                PermissionPool.getInstance().createPermissionUser(cachedPermissionUser);

            }
            return cachedPermissionUser.hasPermission(inName);

        }

        @Override
        public void recalculatePermissions() {
            this.perms = new HashMap<>();

            if (player == null || Bukkit.getPlayer(this.player.getName()) == null || !player.isOnline()) {
                return;
            }

            try {
                for (String s : PermissionPool.getInstance().loadPermissions(player.getUniqueId())) {
                    perms.put(s, new PermissionAttachmentInfo(this, s, new PermissionAttachment(this.pluginInstance, this), true));
                }
            } catch (NullPointerException e) {
                tries += 1;
                Scheduler.runtimeScheduler().schedule(this::recalculatePermissions, 5L);
                if (tries >= 5) {
                    System.out.println("[CloudPlugin] Something went wrong while recalculating permissions of a player!");
                    tries = 0;
                }
            }
        }

    }
}
