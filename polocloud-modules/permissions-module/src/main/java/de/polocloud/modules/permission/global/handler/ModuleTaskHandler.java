package de.polocloud.modules.permission.global.handler;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.PoloCloudPermissionModule;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionGroup;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;

public class ModuleTaskHandler implements IMessageListener<Task> {

    @Override
    public void handleMessage(Task task, long startTime) throws Exception {
        String name = task.getName();
        JsonData document = task.getDocument();

        PermissionPool permissionPool = PermissionPool.getInstance();

        if (name.equalsIgnoreCase(PoloCloudPermissionModule.TASK_NAME_UPDATE_POOL)) {

            SimplePermissionPool pool = document.getObject("pool", SimplePermissionPool.class);
            Guice.bind(PermissionPool.class).toInstance(pool);

        } else if (name.equalsIgnoreCase(PoloCloudPermissionModule.TASK_NAME_UPDATE_GROUP)) {

            IPermissionGroup group = document.getObject("group", SimplePermissionGroup.class);
            group.update();
            permissionPool.update();

        } else if (name.equalsIgnoreCase(PoloCloudPermissionModule.TASK_NAME_UPDATE_USER)) {

            IPermissionUser permissionUser = document.getObject("user", SimplePermissionUser.class);
            permissionPool.updatePermissionUser(permissionUser);
            permissionPool.update();
        }
    }
}
