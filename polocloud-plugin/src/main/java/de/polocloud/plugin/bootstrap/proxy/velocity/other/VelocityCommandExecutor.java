package de.polocloud.plugin.bootstrap.proxy.velocity.other;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.plugin.bootstrap.proxy.velocity.VelocityBootstrap;
import net.kyori.adventure.text.Component;

public class VelocityCommandExecutor implements ConsoleExecutor {

    @Override
    public void runCommand(String command) {
        ConsoleCommandSource consoleCommandSource = VelocityBootstrap.getInstance().getServer().getConsoleCommandSource();
        VelocityBootstrap.getInstance().getServer().getCommandManager().executeImmediatelyAsync(consoleCommandSource, command);
    }

    @Override
    public void sendMessage(String text) {
        ConsoleCommandSource consoleCommandSource = VelocityBootstrap.getInstance().getServer().getConsoleCommandSource();
        consoleCommandSource.sendMessage(Component.text(text));
    }

    @Override
    public ExecutorType getType() {
        return ExecutorType.CONSOLE;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
