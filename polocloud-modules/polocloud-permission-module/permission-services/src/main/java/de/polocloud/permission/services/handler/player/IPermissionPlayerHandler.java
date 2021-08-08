package de.polocloud.permission.services.handler.player;

import com.google.common.util.concurrent.ListenableFuture;
import de.polocloud.permission.api.permission.Permission;
import de.polocloud.permission.api.player.IPermissionPlayer;
import de.polocloud.permission.api.player.PlayerGroupInfo;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface IPermissionPlayerHandler {

    void save(IPermissionPlayer permissionPlayer);

    void update(IPermissionPlayer permissionPlayer, Set<Permission> permissions, Set<PlayerGroupInfo> infos);

    void sync(IPermissionPlayer permissionPlayer);

    void updateName(IPermissionPlayer permissionPlayer, String name);

    boolean existsPlayer(UUID uuid);

    boolean existsPlayer(String name);

    IPermissionPlayer getCachedPermissionPlayer(UUID uuid);

    IPermissionPlayer getCachedPermissionPlayer(String name);

    IPermissionPlayer getPermissionPlayer(UUID uuid);

    IPermissionPlayer getPermissionPlayer(String name);

    Collection<IPermissionPlayer> getAllCachedPermissionPlayers();

    ListenableFuture<IPermissionPlayer> getSqlPermissionPlayer(UUID uuid);

    ListenableFuture<IPermissionPlayer> getSqlPermissionPlayer(String name);


}
