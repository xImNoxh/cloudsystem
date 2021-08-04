package de.polocloud.bootstrap.module;

import com.google.gson.Gson;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MasterModuleLoader {

    private Gson gson;
    private ModuleCache cache;
    private File file;

    public MasterModuleLoader(ModuleCache cache) {
        this.gson = new Gson();
        this.cache = cache;
        this.file = new File("modules/");
    }

    public void loadModules(boolean prefix, ModuleData... ignored) {
        if (!file.exists()) {
            file.mkdir();
        }

        List<ModuleData> moduleData = findModuleData(file, ignored);

        for (ModuleData data : moduleData) {
            try {
                long time = System.currentTimeMillis();
                Class<?> aClass = getClass().getClassLoader().loadClass(Module.class.getName());
                ModuleClassLoader loader = new ModuleClassLoader(new URL[]{data.getFile().toURL()}, Thread.currentThread().getContextClassLoader(), cache);

                Class<?> cl = loader.loadClass(data.getMain());

                Class[] interfaces = cl.getInterfaces();
                boolean isPlugin = false;

                for (int y = 0; y < interfaces.length && !isPlugin; y++) {
                    if (interfaces[y].equals(Module.class)) {
                        isPlugin = true;
                    }
                }

                Module module = (Module) CloudAPI.getInstance().getGuice().getInstance(cl);
                cache.put(module, new ModuleLocalCache(loader, data));
                module.onLoad();
                Logger.log(LoggerType.INFO, (prefix ? Logger.PREFIX : "") + "The module is now " +
                    ConsoleColors.LIGHT_BLUE.getAnsiCode() + data.getName() + ConsoleColors.GRAY.getAnsiCode() + " loaded (Starting time: " + (System.currentTimeMillis() - time) + "ms)");
            } catch (MalformedURLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public ModuleData loadModuleData(File file) {
        if (!file.exists() || !file.isFile() || !file.getName().endsWith(".jar")) {
            return null;
        }
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry("module.json");
            if (entry == null) {
                throw new FileNotFoundException("Cannot find \"module.json\" file");
            }
            try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry))) {
                ModuleData module = gson.fromJson(reader, ModuleData.class);
                module.setFile(file);
                return module;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void loadModule(File file) {
        ModuleData moduleData = loadModuleData(file);
        if (moduleData == null) {
            return;
        }
        try {
            long time = System.currentTimeMillis();
            Class<?> aClass = getClass().getClassLoader().loadClass(Module.class.getName());
            ModuleClassLoader loader = new ModuleClassLoader(new URL[]{moduleData.getFile().toURL()}, Thread.currentThread().getContextClassLoader(), cache);

            Class<?> cl = loader.loadClass(moduleData.getMain());

            Class[] interfaces = cl.getInterfaces();
            boolean isPlugin = false;

            for (int y = 0; y < interfaces.length && !isPlugin; y++) {
                if (interfaces[y].equals(Module.class)) {
                    isPlugin = true;
                }
            }

            Module module = (Module) CloudAPI.getInstance().getGuice().getInstance(cl);
            cache.put(module, new ModuleLocalCache(loader, moduleData));
            module.onLoad();
            Logger.log(LoggerType.INFO, Logger.PREFIX + "The module » " +
                ConsoleColors.LIGHT_BLUE.getAnsiCode() + moduleData.getName() + ConsoleColors.GRAY.getAnsiCode() + " is now loaded (Starting time: " + (System.currentTimeMillis() - time) + "ms)");
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<ModuleData> findModuleData(File directory, ModuleData... ignored) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("directory has to be a directory");
        }

        List<ModuleData> moduleData = new ArrayList<>();

        File[] files = directory.listFiles();

        Arrays.stream(files).filter(key -> key.isFile() && key.exists() && key.getName().endsWith(".jar")).forEach(it -> {
            try (JarFile jarFile = new JarFile(it)) {
                JarEntry entry = jarFile.getJarEntry("module.json");
                if (entry == null) {
                    throw new FileNotFoundException("Cannot find \"module.json\" file");
                }
                try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry))) {
                    ModuleData module = gson.fromJson(reader, ModuleData.class);
                    module.setFile(it);
                    if (Arrays.stream(ignored).noneMatch(b -> b.getName().equalsIgnoreCase(module.getName()) && b.getMain().equalsIgnoreCase(module.getName()))) {
                        moduleData.add(module);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return moduleData;
    }

}
