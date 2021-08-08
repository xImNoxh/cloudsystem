package de.polocloud.permission.services.bootstrap.spigot;

import de.polocloud.permission.api.player.IPermissionPlayer;
import de.polocloud.permission.services.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;


public class PlayerPermissibleBase extends PermissibleBase {

    private IPermissionPlayer permissionPlayer;

    public PlayerPermissibleBase(Player player) {
        super(player);
        this.permissionPlayer = Permissions.getInstance().getPermissionPlayerHandler().getPermissionPlayer(player.getUniqueId());
    }

    @Override
    public boolean hasPermission(String inName) {
        return permissionPlayer.hasPermission(inName);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return permissionPlayer.hasPermission(perm.getName());
    }

    @Override
    public boolean isPermissionSet(String name) {
        return permissionPlayer.hasPermission(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return permissionPlayer.hasPermission(perm.getName());
    }


}
