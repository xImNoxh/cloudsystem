package de.polocloud.api.module;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.inject.InjectedObject;
import de.polocloud.api.module.info.ModuleInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter @Getter
public abstract class CloudModule extends InjectedObject {

    /**
     * The directory of this module
     */
    protected File dataDirectory;

    /**
     * The file of this module
     */
    protected File moduleFile;

    public CloudModule() {
        this.dataDirectory = new File(FileConstants.MASTER_MODULES, info().name() + "/");
    }

    /**
     * Registers all tasks for a {@link CloudModule}
     *
     * @param classObject the class object to register all tasks in
     */
    public void registerTask(Object classObject) {
        PoloCloudAPI.getInstance().getModuleHolder().registerModuleTasks(this, classObject);
    }

    /**
     * Gets an {@link ModuleInfo} of this module
     */
    public ModuleInfo info() {
        return getClass().isAnnotationPresent(ModuleInfo.class) ? getClass().getAnnotation(ModuleInfo.class) : null;
    }

}
