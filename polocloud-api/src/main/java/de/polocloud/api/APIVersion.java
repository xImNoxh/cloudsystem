package de.polocloud.api;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface APIVersion {

    /**
     * The version of the api
     */
    String version();

    /**
     * THe discord link
     */
    String discord();

    /**
     * The state identifier
     */
    String identifier();

    /**
     * All main devs of this cloud
     */
    String[] developers();

}
