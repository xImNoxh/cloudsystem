package de.polocloud.api.command.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The name of the command that the method will handle.
     * No spaces allowed.
     *
     * @return the name
     */
    String name();

    /**
     * The list of aliases.
     *
     * @return a list of aliases
     */
    String[] aliases() default {};

    /**
     * Command help, otherwise known as usage which displays
     * the arguments for the command.
     *
     * @return the help message
     */
    String usage() default "";

    /**
     * What the command does.
     *
     * @return the command description
     */
    String description() default "";
}
