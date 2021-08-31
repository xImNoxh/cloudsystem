
package de.polocloud.api.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add to a variable/array parameter to restrict the minimum number of values it can have.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arguments {

    /**
     * The minimum required argument amount
     */
    int min() default -1;

    /**
     * The maximum allowed argument amount
     */
    int max() default -1;

    /**
     * The only args[0] that are allowed
     * so if the first argument is "test" and it is in this
     * list it will execute the command, otherwise it will
     * send the provided message below
     */
    String[] onlyFirstArgs() default {};

    /**
     * If usage should be sent after lacking
     */
    boolean showUsage() default false;

    /**
     * The message that will be sent if lacked
     */
    String[] message() default "Enter arguments in range between %min% - %max%!";

}
