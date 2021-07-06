package de.polocloud.master;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CommandPool;
import de.polocloud.database.DatabaseService;
import de.polocloud.master.group.GroupService;
import de.polocloud.master.protocol.commands.CommandPoolImpl;
import de.polocloud.master.protocol.commands.CommandService;
import de.polocloud.master.protocol.netty.NettyService;

public class CloudAPIImpl extends CloudAPI {

    private CommandPoolImpl commandPool;
    private CommandService commandService;

    private DatabaseService databaseService;
    private NettyService nettyService;

    private GroupService groupService;

    public CloudAPIImpl() {
        this.commandPool = new CommandPoolImpl();
        this.commandService = new CommandService();

        this.databaseService = new DatabaseService();
        this.nettyService = new NettyService();

        this.groupService = new GroupService();

        while (true){

        }

    }

    public NettyService getNettyService() {
        return nettyService;
    }

    public CommandService getCommandService() {
        return commandService;
    }

    @Override
    public CommandPool getCommandPool() {
        return this.commandPool;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }
}
