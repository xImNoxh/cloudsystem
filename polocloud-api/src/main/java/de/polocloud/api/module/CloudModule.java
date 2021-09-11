package de.polocloud.api.module;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.guice.own.GuiceObject;
import de.polocloud.api.module.info.ModuleInfo;

import java.io.File;

public abstract class CloudModule extends GuiceObject {

    protected File dataDirectory;

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

    public void setDataDirectory(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public void setModuleFile(File moduleFile) {
        this.moduleFile = moduleFile;
    }

    public File getModuleFile() {
        return moduleFile;
    }

    public File getDataDirectory() {
        return dataDirectory;
    }
}
