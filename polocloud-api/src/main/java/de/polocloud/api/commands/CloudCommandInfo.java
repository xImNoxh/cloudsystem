package de.polocloud.api.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CloudCommandInfo {

    /**
     * The name of the command
     */
    String name();

    /**
     * The description of this command
     */
    String description();

    /**
     * The allowed aliases of this command
     */
    String[] aliases();

    /**
     * The command type of this command
     */
    CommandType commandType();

}
