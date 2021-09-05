package de.polocloud.api.module.info;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface ModuleInfo {

    /**
     * The main class of the module
     */
    Class<? extends CloudModule> main();

    /**
     * The name of the module (no spaces are allowed)
     */
    String name();

    /**
     * The version of this module
     */
    String version();

    /**
     * The description of this module
     */
    String description() default "No description provided";

    /**
     * All the authors that contributed to this module
     */
    String[] authors();

    /**
     * Other modules it depends on
     * (Name of the depending modules)
     */
    String[] depends() default {};

    /**
     * The server types this module will be copied to
     */
    ModuleCopyType[] copyTypes();

    /**
     * If this module is reloadable
     */
    boolean reloadable() default true;

}
