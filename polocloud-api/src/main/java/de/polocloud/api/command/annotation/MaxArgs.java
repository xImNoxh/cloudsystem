
package de.polocloud.api.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add to a variable/array parameter to restrict the maximum number of values it can have.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxArgs {

    /**
     * The amount of args at maximum
     */
    int value();

    /**
     * If usage should be sent after lacking
     */
    boolean showUsage() default false;

    /**
     * The message that will be sent if lacked
     */
    String[] message() default "Enter at maximum %args% arguments!";

}
