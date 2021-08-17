package de.polocloud.api.commands;

public abstract class CloudCommand {

    /**
     * The name of the command
     */
    private final String name;

    /**
     * The allowed aliases of this command
     */
    private final String[] aliases;

    /**
     * The description of this command
     */
    private final String description;

    /**
     * The command type of this command
     */
    private final CommandType commandType;

    public CloudCommand() {
        CloudCommandInfo annotation = getClass().getAnnotation(CloudCommandInfo.class);
        if (annotation == null) {
            throw new UnsupportedOperationException("Can not register Command (" + getClass().getName() + ") because it does not have a @" + CloudCommandInfo.class.getSimpleName() + "-Annotation!");
        }
        this.name = annotation.name();
        this.aliases = annotation.aliases();
        this.description = annotation.description();
        this.commandType = annotation.commandType();
    }

    /**
     * Handles this command when a given {@link ICommandExecutor} executed
     * this command with given arguments as {@link String[]}
     *
     * @param sender the sender
     * @param args the arguments provided
     */
    public abstract void execute(ICommandExecutor sender, String[] args);

    public String getName() {
        return name;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getDescription() {
        return description;
    }

}
