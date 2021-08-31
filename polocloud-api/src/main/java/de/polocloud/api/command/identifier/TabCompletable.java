package de.polocloud.api.command.identifier;

import de.polocloud.api.command.executor.CommandExecutor;

import java.util.List;

public interface TabCompletable {

    /**
     * Called when tabbing in console or ingame
     *
     * @param args the args that are provided
     * @return tabCompletions
     */
    List<String> onTabComplete(CommandExecutor executor, String[] args);

}
