package de.polocloud.api.commands;

import java.util.List;

public interface ICommandPool {

    /**
     * Registers a {@link CloudCommand} instance
     *
     * @param cloudCommand the command to register
     */
    void registerCommand(CloudCommand cloudCommand);


    /**
     * Unregisters a {@link CloudCommand} instance
     *
     * @param cloudCommand the command to register
     */
    void unregisterCommand(CloudCommand cloudCommand);

    /**
     * Gets a list of al cached {@link CloudCommand}s
     */
    List<CloudCommand> getAllCachedCommands();
}
