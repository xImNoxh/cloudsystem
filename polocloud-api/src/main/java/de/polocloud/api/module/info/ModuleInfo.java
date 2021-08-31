package de.polocloud.api.module.info;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

//TODO
public @interface ModuleInfo {

    Class<? extends CloudModule> main();

    String name();

    String version();

    String description() default "No description provided";

    String[] authors();

    String[] depends() default {};

    ModuleCopyType[] copyTypes();

    boolean reloadable() default true;

}
