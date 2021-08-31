package de.polocloud.api.module;

import de.polocloud.api.module.info.ModuleState;

import java.util.List;

public interface IModuleHolder {

    /**
     * Gets a {@link CloudModule} by its name
     *
     * @param name the name
     * @return module or null if not found
     */
    CloudModule getModule(String name);

    /**
     * Gets a {@link List} of all loaded {@link CloudModule}s
     */
    List<CloudModule> getModules();

    /**
     * Registers all tasks for a {@link CloudModule}
     *
     * @param module the module
     * @param objectClass the class
     */
    void registerModuleTasks(CloudModule module, Object objectClass);

    /**
     * Calls all tasks for a {@link CloudModule}
     *
     * @param module the module
     * @param state the current state
     */
    void callTasks(CloudModule module, ModuleState state);
}
