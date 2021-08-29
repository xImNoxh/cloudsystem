package de.polocloud.bootstrap.module;

import de.polocloud.api.module.CloudModule;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleCache extends ConcurrentHashMap<CloudModule, ModuleLocalCache> {

    public void unloadModules() {
        for (CloudModule module : this.keySet()) {
            URLClassLoader classLoader = this.get(module).getLoader();

            if (module.canReload()) {

                module.onShutdown();

                String moduleName = get(module).getModuleData().getName();

                remove(module);
                try {
                    classLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                PoloLogger.getInstance().noPrefix().log(LogLevel.INFO, "The module §b" + moduleName + " §7unloaded...");
            }
        }
    }

    public void unloadModule(CloudModule module) {
        try {
            if (module == null || !containsKey(module)) {
                return;
            }

            URLClassLoader urlClassLoader = get(module).getLoader();
            String moduleName = get(module).getModuleData().getName();

            remove(module);

            urlClassLoader.close();
            PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "unloaded module » " + ConsoleColors.LIGHT_BLUE + moduleName + ConsoleColors.GRAY + "!");
        } catch (IOException exception) {
            exception.printStackTrace();
            PoloLogger.print(LogLevel.ERROR, ConsoleColors.RED + "Failed to unload module » " + ConsoleColors.LIGHT_BLUE + get(module).getModuleData().getName() + ConsoleColors.RED + "!" + ConsoleColors.GRAY);
        }
    }

    public CloudModule getModuleByName(String name) {
        return this.keySet().stream().filter(module -> get(module).getModuleData().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    protected Class<?> findModuleClass(String name) throws ClassNotFoundException {
        for (ModuleLocalCache value : values()) {
            Class<?> clazz = value.getLoader().findClass_(name, false);
            if (clazz != null) {
                return clazz;
            }
        }
        throw new ClassNotFoundException(name);
    }

}
