
package de.polocloud.api.command.annotation;


import de.polocloud.api.command.executor.ExecutorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandExecutors {

    ExecutorType[] value();

}
