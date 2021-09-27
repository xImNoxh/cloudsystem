package de.polocloud.modules.permission;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.database.api.Database;
import de.polocloud.api.inject.PoloInject;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.api.messaging.IMessageChannel;
import de.polocloud.api.util.Task;
import de.polocloud.modules.permission.cloudside.ModuleCloudSidePermissionListener;
import de.polocloud.modules.permission.cloudside.ModuleCloudSideServerStartListener;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.handler.ModuleTaskHandler;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.*;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import de.polocloud.modules.permission.global.command.PermsCommand;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Getter
public class PermsModule {

    /**
     * The instance
     */
    @Getter
    private static PermsModule instance;

    /**
     * The database for all users
     */
    private final Database<SimplePermissionUser> userDatabase;

    /**
     * The database for all groups
     */
    private final Database<SimplePermissionGroup> groupDatabase;

    /**
     * The message channel to transfer all tasks
     */
    private final IMessageChannel<Task> messageChannel;

    //All tasks and their names
    public static final String TASK_NAME_UPDATE_POOL = "task-update-pool";
    public static final String TASK_NAME_UPDATE_USER = "task-update-user";
    public static final String TASK_NAME_UPDATE_GROUP = "task-update-group";

    public PermsModule(ModuleBootstrap bootstrap) {
        instance = this;
        this.userDatabase = PoloCloudAPI.getInstance().getType().isPlugin() ? null : new Database<>("permission-users", new File(bootstrap.getDataDirectory(), "permission-users"), SimplePermissionUser.class);
        this.groupDatabase = PoloCloudAPI.getInstance().getType().isPlugin() ? null : new Database<>("permission-groups", new File(bootstrap.getDataDirectory(), "permission-groups"), SimplePermissionGroup.class);

        this.messageChannel = PoloCloudAPI.getInstance().getMessageManager().registerChannel(Task.class, "permission-module-tasks");
        this.messageChannel.registerListener(new ModuleTaskHandler());

        if (this.groupDatabase != null && this.userDatabase != null) {
            if (this.groupDatabase.getEntries().isEmpty()) {

                SimplePermissionGroup playerGroup = new SimplePermissionGroup("Player", 0, true, Collections.singletonList("cloud.defaultpermission"), new SimplePermissionDisplay(MinecraftColor.GRAY, "§aPlayer §8▏ §7", "§7", ""), new ArrayList<>());
                SimplePermissionGroup adminGroup = new SimplePermissionGroup("Admin", 9999, false, Arrays.asList("*", "cloud.use", "cloud.maintenance", "cloud.server.full.connect", "cloud.stop", "cloud.notify", "cloud.fulljoin"), new SimplePermissionDisplay(MinecraftColor.DARK_RED, "§4Admin §8▏ §7", "§7", ""), Collections.singletonList("Player"));

                this.groupDatabase.insert(playerGroup.getName(), playerGroup);
                this.groupDatabase.insert(adminGroup.getName(), adminGroup);
            }
            PoloLogger.getInstance().noDisplay().log(LogLevel.INFO, "§7PermissionModule loaded §b" + this.userDatabase.getEntries().size() + " §3PermissionUsers §7and §3" + this.groupDatabase.getEntries().size() + " §3PermissionGroups§7!");
        }
        PoloInject.bind(PermissionPool.class).toInstance(new SimplePermissionPool());
    }

    /**
     * Loads this module
     */
    public void load() {

        if (PoloCloudAPI.getInstance().getType() != PoloType.PLUGIN_SPIGOT) {
            PoloCloudAPI.getInstance().getCommandManager().registerCommand(new PermsCommand());
        }
        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudAPI.getInstance().getEventManager().registerListener(new ModuleCloudSideServerStartListener());
            PoloCloudAPI.getInstance().getEventManager().registerListener(new ModuleCloudSidePermissionListener());
        }
    }

    /**
     * Enables this module
     */
    public void enable() {
    }

    /**
     * Reloads this module
     */
    public void reload() {
        PermissionPool.getInstance().update();
    }

    /**
     * Shuts down this module
     */
    public void shutdown() {
    }
}
