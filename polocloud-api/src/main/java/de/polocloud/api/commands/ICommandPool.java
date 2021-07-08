package de.polocloud.api.commands;

import java.util.List;

public interface ICommandPool {

    void registerCommand(CloudCommand cloudCommand);

    List<CloudCommand> getAllCachedCommands();

    void unregisterCommand(CloudCommand cloudCommand);

}
