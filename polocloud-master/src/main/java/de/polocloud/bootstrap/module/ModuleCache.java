package de.polocloud.bootstrap.module;

import de.polocloud.api.module.Module;
import de.polocloud.logger.log.Logger;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleCache extends ConcurrentHashMap<Module, ModuleClassLoader> {

    public void unloadModules() {
        for (Module module : this.keySet()) {
            URLClassLoader classLoader = this.get(module);
            remove(module);
            try {
                classLoader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.log(Logger.PREFIX + "Module ?? unloaded...");
        }
    }

    protected Class<?> findModuleClass(String name) throws ClassNotFoundException {
        for (ModuleClassLoader value : values()) {
            Class<?> clazz = value.findClass_(name, false);
            if (clazz != null) {
                return clazz;
            }
        }
        throw new ClassNotFoundException(name);
    }

}
