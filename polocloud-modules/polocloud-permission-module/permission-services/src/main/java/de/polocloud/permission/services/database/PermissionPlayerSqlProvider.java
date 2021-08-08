package de.polocloud.permission.services.database;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.permission.Permission;
import de.polocloud.permission.api.player.PlayerGroupInfo;
import de.polocloud.permission.services.database.provider.ISqlProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;


public class PermissionPlayerSqlProvider extends ISqlProvider {

    public PermissionPlayerSqlProvider() {
        super("permission_players");
    }

    @Override
    public void createTable() {
        CompletableFuture.supplyAsync(() -> getSqlExecutor().executeUpdate("CREATE TABLE IF NOT EXISTS "+getTable()+" (uuid VARCHAR(64), name VARCHAR(64), permissions TEXT, groups TEXT)"));
    }

    public void createPlayer(UUID uuid, String name, IPermissionGroup defaultGroup) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO "+getTable()+" (uuid, name, permissions, groups) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setString(3, "");
                statement.setString(4, defaultGroup.getName() +":-1;");
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public void updateName(UUID uuid, String name) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE "+getTable()+" SET name='"+name+"' WHERE uuid='"+uuid.toString()+"'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public boolean existsPlayer(UUID uuid) {
        return getSqlExecutor().executeQuery("SELECT name FROM "+getTable()+" WHERE uuid='"+uuid.toString()+"'", ResultSet::next, false);
    }

    public boolean existsPlayer(String name) {
        return getSqlExecutor().executeQuery("SELECT name FROM "+getTable()+" WHERE name='"+name+"'", ResultSet::next, false);
    }

    public void setPermissions(UUID uuid, Collection<Permission> permissions) {
        StringBuilder stringBuilder = new StringBuilder();
        for(Permission permission : permissions) {
            stringBuilder.append(permission.getPermissionString()).append(":").append(permission.getExpireTime()).append(";");
        }
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE "+getTable()+" SET permissions='"+stringBuilder+"' WHERE uuid='"+uuid.toString()+"'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    private Map<UUID, ListenableFuture<String>> permissionStringMap = new HashMap<>();

    public ListenableFuture<String> getPermissionString(UUID uuid) {
        if(permissionStringMap.containsKey(uuid)) return permissionStringMap.get(uuid);
        SettableFuture<String> future = SettableFuture.create();
        permissionStringMap.put(uuid, future);
        future.set(getSqlExecutor().executeQuery("SELECT permissions FROM "+getTable()+" WHERE uuid='"+uuid.toString()+"'", (result) -> result.next() ? result.getString("permissions") : "", ""));
        return future;
    }

    public Collection<Permission> getPermissions(UUID uuid) {
        Collection<Permission> permissions = new CopyOnWriteArrayList<>();
        Futures.addCallback(getPermissionString(uuid), new FutureCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if(s.isEmpty()) return;
                String[] permissionSplit = s.split(";");
                for(String permission : permissionSplit) {
                    String permissionName = permission.split(":")[0];
                    long expireTime = Long.parseLong(permission.split(":")[1]);
                    permissions.add(new Permission(permissionName, expireTime));
                }
                permissionStringMap.remove(uuid);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("failed to load permissions from "+uuid.toString());
                permissionStringMap.remove(uuid);
            }
        });
        return permissions;
    }

    public void setPermissionGroups(UUID uuid, Collection<PlayerGroupInfo> permissionGroups) {
        StringBuilder stringBuilder = new StringBuilder();
        for(PlayerGroupInfo info : permissionGroups) {
            stringBuilder.append(info.getPermissionGroup().getName()).append(":").append(info.getExpireTime()).append(";");
        }
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE "+getTable()+" SET groups='"+stringBuilder+"' WHERE uuid='"+uuid.toString()+"'")){
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public ListenableFuture<String> getPermissionGroupsString(UUID uuid) {
        SettableFuture<String> future = SettableFuture.create();
        future.set(getSqlExecutor().executeQuery("SELECT groups FROM "+getTable()+" WHERE uuid='"+uuid+"'", (result) -> result.next() ? result.getString("groups") : "", ""));
        return future;
    }


}
