package de.polocloud.api.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class CloudCommand {

    private String name;
    private String[] aliases;
    private String description;
    private CommandType commandType;

    public CloudCommand() {
        name = getClass().getAnnotation(Info.class).name();
        aliases = getClass().getAnnotation(Info.class).aliases();
        description = getClass().getAnnotation(Info.class).description();
        commandType = getClass().getAnnotation(Info.class).commandType();
    }

    public abstract void execute(ICommandExecutor sender, String[] args);


    public String getName() {
        return name;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Info {

        String name();
        String description();
        String[] aliases();
        CommandType commandType();
    }

}
