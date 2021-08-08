package de.polocloud.permission.services.bootstrap.spigot;

import de.polocloud.permission.api.player.IPermissionPlayer;
import de.polocloud.permission.api.player.PermissionPlayer;
import de.polocloud.permission.api.player.PlayerGroupInfo;
import de.polocloud.permission.services.Permissions;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.lang.reflect.Field;

public class SpigotCollectiveListener implements Listener {

    @EventHandler
    public void handle(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if(Permissions.getInstance().getPermissionPlayerHandler().existsPlayer(player.getUniqueId())) {
            Permissions.getInstance().getPermissionPlayerHandler().getPermissionPlayer(player.getUniqueId());
        } else {
            IPermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId(), player.getName());
            permissionPlayer.addPermissionGroup(new PlayerGroupInfo(Permissions.getInstance().getPermissionGroupHandler().getDefaultGroup(), -1L));
            Permissions.getInstance().getPermissionPlayerHandler().getAllCachedPermissionPlayers().add(permissionPlayer);
            Permissions.getInstance().getPermissionPlayerHandler().save(permissionPlayer);
        }

        try {
            Field field = CraftHumanEntity.class.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(event.getPlayer(), new PlayerPermissibleBase(event.getPlayer()));
            field.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }

    }


}
