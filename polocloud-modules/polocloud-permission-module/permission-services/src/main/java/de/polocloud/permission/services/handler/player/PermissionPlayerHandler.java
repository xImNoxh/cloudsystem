package de.polocloud.permission.services.handler.player;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.polocloud.permission.api.permission.Permission;
import de.polocloud.permission.api.player.IPermissionPlayer;
import de.polocloud.permission.api.player.PermissionPlayer;
import de.polocloud.permission.api.player.PlayerGroupInfo;
import de.polocloud.permission.services.Permissions;
import de.polocloud.permission.services.database.PermissionPlayerSqlProvider;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PermissionPlayerHandler extends PermissionPlayerSqlProvider implements IPermissionPlayerHandler {

    private final Map<UUID, ListenableFuture<IPermissionPlayer>> futureMap;
    private final Collection<IPermissionPlayer> permissionPlayerCache;

    public PermissionPlayerHandler() {
        this.permissionPlayerCache = new CopyOnWriteArrayList<>();
        this.futureMap = new HashMap<>();
    }

    @Override
    public void save(IPermissionPlayer permissionPlayer) {
        if (!existsPlayer(permissionPlayer.getUniqueId()))
            createPlayer(permissionPlayer.getUniqueId(), permissionPlayer.getName(), Permissions.getInstance().getPermissionGroupHandler().getDefaultGroup());
        setPermissions(permissionPlayer.getUniqueId(), permissionPlayer.getAllNotExpiredPermissions());
        setPermissionGroups(permissionPlayer.getUniqueId(), permissionPlayer.getAllNotExpiredPlayerGroupInfos());
    }

    @Override
    public void sync(IPermissionPlayer permissionPlayer) {
        Permissions.getInstance().getMessageHandler().sendPermissionPlayerInfo(permissionPlayer);
    }

    @Override
    public void updateName(IPermissionPlayer permissionPlayer, String name) {
        this.updateName(permissionPlayer.getUniqueId(), name);
    }

    @Override
    public void update(IPermissionPlayer permissionPlayer, Set<Permission> permissions, Set<PlayerGroupInfo> infos) {
        permissionPlayer.clearPlayerGroupInfos();
        permissionPlayer.clearPermissions();

        for (Permission permission : permissions) permissionPlayer.addPermission(permission);
        for (PlayerGroupInfo info : infos) permissionPlayer.addPermissionGroup(info);
    }

    @Override
    public IPermissionPlayer getCachedPermissionPlayer(UUID uuid) {
        return this.permissionPlayerCache.stream().filter(player -> player.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public IPermissionPlayer getCachedPermissionPlayer(String name) {
        return this.permissionPlayerCache.stream().filter(player -> player.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public ListenableFuture<IPermissionPlayer> getSqlPermissionPlayer(UUID uuid) {
        if (futureMap.containsKey(uuid)) return futureMap.get(uuid);

        SettableFuture<IPermissionPlayer> future = SettableFuture.create();
        futureMap.put(uuid, future);
        if (!existsPlayer(uuid)) {
            future.set(null);
            return future;
        }

        IPermissionPlayer player = new PermissionPlayer(uuid, getNameFromPlayerUniqueId(uuid));
        for (PlayerGroupInfo info : getPermissionGroups(uuid)) {
            player.addPermissionGroup(info);
        }
        for (Permission permission : getPermissions(uuid)) {
            player.addPermission(permission);
        }
        permissionPlayerCache.add(player);
        future.set(player);
        return future;
    }

    @Override
    public ListenableFuture<IPermissionPlayer> getSqlPermissionPlayer(String name) {
        UUID uuid = getUUIDFromPlayerName(name);

        if (futureMap.containsKey(uuid)) return futureMap.get(uuid);

        SettableFuture<IPermissionPlayer> future = SettableFuture.create();
        futureMap.put(uuid, future);

        if (!existsPlayer(uuid)) {
            future.set(null);
            return future;
        }

        IPermissionPlayer player = new PermissionPlayer(uuid, name);
        for (PlayerGroupInfo info : getPermissionGroups(uuid)) {
            player.addPermissionGroup(info);
        }
        for (Permission permission : getPermissions(uuid)) {
            player.addPermission(permission);
        }
        permissionPlayerCache.add(player);
        future.set(player);
        return future;
    }

    @Override
    public IPermissionPlayer getPermissionPlayer(UUID uuid) {
        if (this.permissionPlayerCache.stream().anyMatch(player -> player.getUniqueId().equals(uuid))) {
            return getCachedPermissionPlayer(uuid);
        }

        final IPermissionPlayer[] player = {null};
        Futures.addCallback(getSqlPermissionPlayer(uuid), new FutureCallback<IPermissionPlayer>() {
            @Override
            public void onSuccess(IPermissionPlayer permissionPlayer) {
                player[0] = permissionPlayer;
                futureMap.remove(uuid);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("no sql player found");
                futureMap.remove(uuid);
            }
        });
        return player[0];
    }

    @Override
    public IPermissionPlayer getPermissionPlayer(String name) {
        if (this.permissionPlayerCache.stream().anyMatch(player -> player.getName().equals(name))) {
            return getCachedPermissionPlayer(name);
        }

        final IPermissionPlayer[] player = {null};
        Futures.addCallback(getSqlPermissionPlayer(name), new FutureCallback<IPermissionPlayer>() {
            @Override
            public void onSuccess(IPermissionPlayer permissionPlayer) {
                player[0] = permissionPlayer;
                futureMap.remove(permissionPlayer.getUniqueId());
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("no sql player found");
                futureMap.remove(getUUIDFromPlayerName(name));
            }
        });
        return player[0];
    }

    public Collection<PlayerGroupInfo> getPermissionGroups(UUID uuid) {
        Collection<PlayerGroupInfo> list = new CopyOnWriteArrayList<>();
        Futures.addCallback(getPermissionGroupsString(uuid), new FutureCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.isEmpty()) return;
                String[] groupSplit = s.split(";");
                for (String group : groupSplit) {
                    String groupName = group.split(":")[0];
                    long expireTime = Long.parseLong(group.split(":")[1]);
                    list.add(new PlayerGroupInfo(Permissions.getInstance().getPermissionGroupHandler().getPermissionGroupByName(groupName), expireTime));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("failed to load groups from " + uuid.toString());
            }
        });
        return list;
    }

    @Override
    public Collection<IPermissionPlayer> getAllCachedPermissionPlayers() {
        return permissionPlayerCache;
    }

    @Override
    public boolean existsPlayer(UUID uuid) {
        return super.existsPlayer(uuid);
    }

    @Override
    public boolean existsPlayer(String name) {
        return super.existsPlayer(name);
    }

    public String getNameFromPlayerUniqueId(UUID uuid) {
        return getSqlExecutor().executeQuery("SELECT name FROM " + getTable() + " WHERE uuid='" + uuid.toString() + "'", (result) -> result.next() ? result.getString("name") : "", "");
    }

    public UUID getUUIDFromPlayerName(String name) {
        return getSqlExecutor().executeQuery("SELECT uuid FROM " + getTable() + " WHERE name='" + name + "'", (result) -> result.next() ? UUID.fromString(result.getString("uuid")) : null, null);
    }


}
