package de.polocloud.api.command.runner;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;

import java.util.Arrays;

public interface ICommandRunner {

    /**
     * Gets whether this string is an alias of this command.
     * (returns false even it is the main command name)
     *
     * @param command the string to test against
     *
     * @return true iff the given parameter is an alias of the command.
     */
    default boolean isAlias(String command) {
        return Arrays.asList(this.getCommand().aliases()).contains(command);
    }

    /**
     * Gets the associated {@link Command}.
     *
     * @return The command.
     */
    Command getCommand();

    /**
     * The {@link CommandListener} instance
     */
    CommandListener getListener();

    /**
     * The usages as String of this {@link ICommandRunner}
     */
    String getUsage();

    /**
     * Runs the command handled by the method wrapped by
     * this command source.
     *
     * @param cmd the command to run
     * @param source the command sender
     * @param args the command arguments
     */
    void runCommand(String cmd, CommandExecutor source, String[] args);

    /**
     * Obtains whether the given class is what this
     * command is contained by.
     *
     * @param cls the class to check
     * @return {@code true} if the class is the class which
     * contains the command
     */
    boolean isContainedBy(Class<? extends CommandListener> cls);

    /**
     * Returns the type of executors {@link ExecutorType}
     */
    ExecutorType[] getAllowedSourceTypes();

}
