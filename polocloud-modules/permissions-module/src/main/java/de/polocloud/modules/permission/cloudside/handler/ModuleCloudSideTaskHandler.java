package de.polocloud.modules.permission.cloudside.handler;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.database.IDatabase;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PermissionModule;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;

public class ModuleCloudSideTaskHandler implements IMessageListener<Task> {

    @Override
    public void handleMessage(Task task, long startTime) throws Exception {
        String name = task.getName();
        JsonData document = task.getDocument();
        IDatabase<SimplePermissionUser> userDatabase = PermissionModule.getInstance().getUserDatabase();

        if (name.equalsIgnoreCase("create-user")) {
            SimplePermissionUser user = document.getObject("user", SimplePermissionUser.class);
            userDatabase.insert(user.getUniqueId().toString(), user);
            PermissionPool.getInstance().getAllCachedPermissionUser().add(user);
            PermissionModule.getInstance().reload();
        } else if (name.equalsIgnoreCase("update-user")) {
            SimplePermissionUser permissionUser = document.getObject("user", SimplePermissionUser.class);
            userDatabase.insert(permissionUser.getUniqueId().toString(), permissionUser);

            PermissionPool.getInstance().getAllCachedPermissionUser().removeIf(user -> user.getName().equalsIgnoreCase(permissionUser.getName()));
            PermissionPool.getInstance().getAllCachedPermissionUser().add(permissionUser);
            PermissionModule.getInstance().reload();
        }
    }
}
