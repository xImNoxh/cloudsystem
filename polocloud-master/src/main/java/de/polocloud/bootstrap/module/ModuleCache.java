package de.polocloud.bootstrap.module;

import de.polocloud.api.event.EventRegistry;
import de.polocloud.api.module.Module;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleCache extends ConcurrentHashMap<Module, ModuleLocalCache> {

    public void unloadModules() {
        for (Module module : this.keySet()) {
            URLClassLoader classLoader = this.get(module).getLoader();

            if (module.onReload()) {

                module.onShutdown();
                EventRegistry.unregisterModuleListener(module);

                String moduleName = get(module).getModuleData().getName();

                remove(module);
                try {
                    classLoader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Logger.log(Logger.PREFIX + "Module " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + moduleName + " unloaded...");
            }
        }
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
