package de.polocloud.api.command;

import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.util.Acceptable;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface ICommandManager {

    /**
     * Enables the filter for this command
     * to only allow certain de.polocloud.modules.smartproxy.moduleside.commands to be executed
     *
     * @param filter the "filter"
     */
    void setFilter(Acceptable<ICommandRunner> filter);

    /**
     * Registers the given instance of the command listener
     * to the registry.
     *
     * <p>Command names are always registered as lowercase.
     * </p>
     *
     * @param listener the listener to register
     */
    void registerCommand(CommandListener listener);

    /**
     * Removes a command listener from the registry, thus
     * causing the handled command to respond as unknown.
     *
     * @param listener the listener to remove
     */
    void unregisterCommand(Class<? extends CommandListener> listener);

    /**
     * Dispatches the given command to its respective
     * listener.
     *
     * @param cmd the command string to dispatch, without a
     * "/"
     * @param source the source to use
     * @return {@code true} if the command was successfully
     * dispatched, {@code false} otherwise, or if there is
     * no such command
     */
    boolean runCommand(String cmd, CommandExecutor source);

    /**
     * A list of all registered {@link ICommandRunner}s
     *
     * @return list
     */
    List<ICommandRunner> getCommands();

    Map<String, ICommandRunner> getCommandsAsMap();

    /**
     * Registers an Arg-Transformer for a given class
     *
     * @param clazz the class
     * @param transformer the transformer-function
     * @param <T> the generic
     */
    <T> void registerTransformer(Class<T> clazz, BiFunction<String, Parameter, T> transformer);

    /**
     * Transforms input with a given class to an object
     * @param input the input string
     * @param parameter the parameter
     * @param clazz the class
     * @param <T> the generic
     * @return object
     * @throws Exception if something goes wrong
     */
    <T> T transform(String input, Parameter parameter, Class<T> clazz) throws Exception;
}
