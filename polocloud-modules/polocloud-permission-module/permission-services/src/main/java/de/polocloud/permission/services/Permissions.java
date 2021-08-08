package de.polocloud.permission.services;

import de.polocloud.permission.api.database.DatabaseCredentials;
import de.polocloud.permission.api.database.adapter.DatabaseService;
import de.polocloud.permission.api.database.scanner.ScannerService;
import de.polocloud.permission.services.handler.group.IPermissionGroupHandler;
import de.polocloud.permission.services.handler.group.PermissionGroupHandler;
import de.polocloud.permission.services.handler.messaging.IMessageHandler;
import de.polocloud.permission.services.handler.messaging.MessageHandler;
import de.polocloud.permission.services.handler.player.IPermissionPlayerHandler;
import de.polocloud.permission.services.handler.player.PermissionPlayerHandler;

public class Permissions {

    private static Permissions instance;
    private final IPermissionGroupHandler permissionGroupHandler;
    private final IPermissionPlayerHandler permissionPlayerHandler;
    private final IMessageHandler messageHandler;

    private DatabaseService databaseService;
    private ScannerService scannerService;

    private final boolean proxy;

    public Permissions(boolean proxy) {
        instance = this;

        this.scannerService = new ScannerService(this.getClass().getClassLoader(), this.databaseService = new DatabaseService());
        databaseService.getDatabaseSQL().connect(DatabaseCredentials.of("127.0.0.1", "root","polocloud-permission",3306,"password"), null);

        this.proxy = proxy;
        this.permissionGroupHandler = new PermissionGroupHandler();
        this.permissionPlayerHandler = new PermissionPlayerHandler();
        this.messageHandler = new MessageHandler();
    }

    public IPermissionGroupHandler getPermissionGroupHandler() {
        return permissionGroupHandler;
    }

    public IPermissionPlayerHandler getPermissionPlayerHandler() {
        return permissionPlayerHandler;
    }

    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    public static Permissions getInstance() {
        return instance;
    }

    public boolean isProxy() {
        return proxy;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public ScannerService getScannerService() {
        return scannerService;
    }
}
