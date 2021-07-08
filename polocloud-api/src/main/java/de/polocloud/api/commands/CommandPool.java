package de.polocloud.api.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandPool extends ArrayList<CloudCommand> implements ICommandPool {

    @Override
    public void registerCommand(CloudCommand cloudCommand) {
        add(cloudCommand);
    }

    @Override
    public List<CloudCommand> getAllCachedCommands() {
        return this;
    }

    @Override
    public void unregisterCommand(CloudCommand cloudCommand) {
        remove(cloudCommand);
    }
}
