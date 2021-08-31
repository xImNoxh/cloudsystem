
package de.polocloud.api.command.executor;


public interface CommandExecutor {

    /**
     * Runs the given command and sends it to the command
     * dispatcher from the implementing dispatcher.
     *
     * @param command the command to handle, without the "/"
     */
    void runCommand(String command);

    /**
     * Sends the command source a message.
     *
     * @param text the message to send
     */
    void sendMessage(String text);

    /**
     * Obtains the command source's type.
     *
     * @return the command source's type
     */
    ExecutorType getType();

    /**
     * Gets whether this CommandSource has the given permission.
     *
     * @param permission The permission.
     * @return True iff the source has the permission.
     */
    boolean hasPermission(String permission);
}
