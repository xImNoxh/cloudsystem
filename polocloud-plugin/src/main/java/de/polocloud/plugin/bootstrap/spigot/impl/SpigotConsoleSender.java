package de.polocloud.plugin.bootstrap.spigot.impl;

import de.polocloud.api.command.executor.SimpleConsoleExecutor;
import org.bukkit.command.CommandSender;

public class SpigotConsoleSender extends SimpleConsoleExecutor {

    private final CommandSender commandSender;

    public SpigotConsoleSender(CommandSender commandSender) {
        this.commandSender = commandSender;

    }

    @Override
    public void sendMessage(String text) {
        commandSender.sendMessage(text);
    }

}
