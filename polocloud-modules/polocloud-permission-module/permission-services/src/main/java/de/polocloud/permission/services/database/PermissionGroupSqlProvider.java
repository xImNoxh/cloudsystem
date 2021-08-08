package de.polocloud.permission.services.database;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import de.polocloud.permission.api.group.IPermissionGroup;
import de.polocloud.permission.api.permission.Permission;
import de.polocloud.permission.services.database.provider.ISqlProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;


public class PermissionGroupSqlProvider extends ISqlProvider {

    public PermissionGroupSqlProvider() {
        super("permission_groups");
    }

    @Override
    public void createTable() {
        CompletableFuture.supplyAsync(() -> getSqlExecutor().executeUpdate("CREATE TABLE IF NOT EXISTS " + getTable() + " (name VARCHAR(64), priority INT, permissions TEXT, inherited_groups TEXT)"));
    }

    public void createGroup(String name, int priority) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO " + getTable() + " (name, priority, permissions, inherited_groups) VALUES (?, ?, ?, ?)")) {
                statement.setString(1, name);
                statement.setInt(2, priority);
                statement.setString(3, "");
                statement.setString(4, "");
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public void deleteGroup(String name) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM " + getTable() + " WHERE name='" + name + "'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public boolean existsSqlGroup(String name) {
        return getSqlExecutor().executeQuery("SELECT name FROM " + getTable() + " WHERE name='" + name + "'", ResultSet::next, false);
    }

    public void setPriority(String name, int priority) {
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE " + getTable() + " SET priority=" + priority + " WHERE name='" + name + "'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public int getPriority(String name) {
        return getSqlExecutor().executeQuery("SELECT priority FROM " + getTable() + " WHERE name='" + name + "'", (result) -> result.next() ? Integer.parseInt(result.getString("priority")) : 0, 0);
    }

    public void setPermissions(String name, Collection<Permission> permissions) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Permission permission : permissions) {
            stringBuilder.append(permission.getPermissionString()).append(":").append(permission.getExpireTime()).append(";");
        }
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE " + getTable() + " SET permissions='" + stringBuilder + "' WHERE name='" + name + "'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public ListenableFuture<String> getPermissionString(String name) {
        SettableFuture<String> future = SettableFuture.create();
        future.set(getSqlExecutor().executeQuery("SELECT permissions FROM " + getTable() + " WHERE name='" + name + "'", (result) -> result.next() ? result.getString("permissions") : "", ""));
        return future;
    }

    public Collection<Permission> getPermissions(String name) {
        Collection<Permission> permissions = new CopyOnWriteArrayList<>();
        Futures.addCallback(getPermissionString(name), new FutureCallback<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.isEmpty()) return;
                String[] permissionSplit = s.split(";");
                for (String permission : permissionSplit) {
                    String permissionName = permission.split(":")[0];
                    long expireTime = Long.parseLong(permission.split(":")[1]);
                    permissions.add(new Permission(permissionName, expireTime));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("failed to load permissions from " + name);
            }
        });
        return permissions;
    }

    public void setInheritedGroups(String name, Collection<IPermissionGroup> permissionGroups) {
        StringBuilder stringBuilder = new StringBuilder();
        for (IPermissionGroup permissionGroup : permissionGroups) {
            stringBuilder.append(permissionGroup.getName()).append(";");
        }
        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.getSqlExecutor().getDatabaseConnector().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE " + getTable() + " SET inherited_groups='" + stringBuilder + "' WHERE name='" + name + "'")) {
                return statement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
                return -1;
            }
        });
    }

    public ListenableFuture<String> getInheritedGroupsString(String name) {
        SettableFuture<String> future = SettableFuture.create();
        future.set(getSqlExecutor().executeQuery("SELECT inherited_groups FROM " + getTable() + " WHERE name='" + name + "'", (result) -> result.next() ? result.getString("inherited_groups") : "", ""));
        return future;
    }

    public ListenableFuture<Collection<String>> getAllPermissionGroupsFromSql() {
        SettableFuture<Collection<String>> future = SettableFuture.create();

        Collection<String> list = getSqlExecutor().executeQuery("SELECT name FROM " + getTable(), (result) -> {
            Collection<String> content = new CopyOnWriteArrayList<>();

            while (result.next()) {
                content.add(result.getString("name"));
            }

            return content;
        }, new CopyOnWriteArrayList<>());

        future.set(list);
        return future;
    }
}
