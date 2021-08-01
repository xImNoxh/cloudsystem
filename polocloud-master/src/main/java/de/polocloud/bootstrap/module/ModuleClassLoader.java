package de.polocloud.bootstrap.module;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class ModuleClassLoader extends URLClassLoader {
    
    private final ModuleCache moduleCache;
    private final Map<String, Class<?>> cachedClasses = Maps.newConcurrentMap();
    private boolean closed = false;

    public ModuleClassLoader(URL[] urls, ClassLoader parent, ModuleCache moduleCache) {
        super(urls, parent);
        this.moduleCache = moduleCache;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (closed) throw new RuntimeException("Can't find class when loader is closed");
        return findClass_(name, true);
    }

    protected Class<?> findClass_(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> clazz = cachedClasses.get(name);
        if (clazz != null) {
            return clazz;
        }

        Class<?> classByName = super.findClass(name);
        if (classByName != null) {
            cachedClasses.put(name, classByName);
            return classByName;
        }

        if (checkGlobal) {
            Class<?> otherModulesClass = moduleCache.findModuleClass(name);
            if (otherModulesClass != null) {
                cachedClasses.put(name, otherModulesClass);
                return otherModulesClass;
            }
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    public void close() throws IOException {
        super.close();
        closed = true;
    }
}
