package de.polocloud.modules.permission.global.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.messaging.IMessageListener;
import de.polocloud.api.util.other.Task;
import de.polocloud.api.util.other.WrappedObject;
import de.polocloud.modules.permission.PermsModule;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.IPermissionUser;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionGroup;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionUser;

public class ModuleTaskHandler implements IMessageListener<Task> {

    @Override
    public void handleMessage(WrappedObject<Task> wrappedObject, long startTime) throws Exception {
        Task task = wrappedObject.unwrap(Task.class);
        String name = task.getName();
        JsonData document = task.getDocument();

        PermissionPool permissionPool = PermissionPool.getInstance();

        if (name.equalsIgnoreCase(PermsModule.TASK_NAME_UPDATE_POOL)) {

            SimplePermissionPool pool = document.getObject("pool", SimplePermissionPool.class);
            PoloCloudAPI.getInstance().getInjector().bind(PermissionPool.class).toInstance(pool);

        } else if (name.equalsIgnoreCase(PermsModule.TASK_NAME_UPDATE_GROUP)) {

            IPermissionGroup group = document.getObject("group", SimplePermissionGroup.class);
            group.update();
            permissionPool.update();

        } else if (name.equalsIgnoreCase(PermsModule.TASK_NAME_UPDATE_USER)) {

            IPermissionUser permissionUser = document.getObject("user", SimplePermissionUser.class);
            permissionPool.updatePermissionUser(permissionUser);
            permissionPool.update();
        }
    }
}
