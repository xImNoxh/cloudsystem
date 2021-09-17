package de.polocloud.api.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {

    /**
     * The name of the field for binding with names
     */
    String value() default "";

    /**
     * The injector type
     */
    Class<?> injector() default Class.class;

    /**
     * If the global registered value should
     * be used if nothing is found for given name
     */
    boolean fallback() default false;
}
