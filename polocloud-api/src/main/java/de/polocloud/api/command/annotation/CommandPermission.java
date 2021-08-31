package de.polocloud.api.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {

    /**
     * The permissions you can have to execute this
     * You don't have to have All the permissions
     * but only one permission is enough to allow execution
     *
     * @return permissions as array
     */
    String[] value();

}
