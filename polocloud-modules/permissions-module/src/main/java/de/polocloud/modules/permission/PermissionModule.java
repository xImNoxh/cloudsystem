package de.polocloud.modules.permission;

import de.polocloud.api.database.DocumentObjectDatabase;
import de.polocloud.api.database.IDatabase;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.modules.permission.api.PermissionPool;
import de.polocloud.modules.permission.api.impl.SimplePermissionGroup;
import de.polocloud.modules.permission.api.impl.SimplePermissionPool;
import de.polocloud.modules.permission.api.impl.SimplePermissionUser;
import de.polocloud.modules.permission.bootstrap.ModuleBootstrap;
import lombok.Getter;

import java.io.File;

@Getter
public class PermissionModule {

    @Getter
    private static PermissionModule instance;

    private final IDatabase<SimplePermissionUser> userDatabase;
    private final IDatabase<SimplePermissionGroup> groupDatabase;

    public PermissionModule(ModuleBootstrap bootstrap) {
        instance = this;
        this.userDatabase = new DocumentObjectDatabase<>("permission-users", new File(bootstrap.getDataDirectory(), "permission-users"), SimplePermissionUser.class);
        this.groupDatabase = new DocumentObjectDatabase<>("permission-groups", new File(bootstrap.getDataDirectory(), "permission-groups"), SimplePermissionGroup.class);

        Guice.bind(PermissionPool.class).toInstance(new SimplePermissionPool());
    }

    public void load() {
    }

    public void enable() {

    }

    public void reload() {

    }

    public void shutdown() {

    }
}
