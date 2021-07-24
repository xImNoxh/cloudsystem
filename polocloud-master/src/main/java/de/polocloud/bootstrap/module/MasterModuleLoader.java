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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MasterModuleLoader {

    private Gson gson = new Gson();

    private File file = new File("modules/");

    public void loadModules() {
        if (!file.exists()) {
            file.mkdir();
        }

        List<ModuleData> moduleData = findModuleData(file);

        for (ModuleData data : moduleData) {
            try {
                Class<?> aClass = getClass().getClassLoader().loadClass(Module.class.getName());
                Class<?> cl = new URLClassLoader(new URL[]{data.getFile().toURL()}, Thread.currentThread().getContextClassLoader()).loadClass(data.getMain());

                Class[] interfaces = cl.getInterfaces();
                boolean isplugin = false;
                for (int y = 0; y < interfaces.length && !isplugin; y++) {
                    if (interfaces[y].equals(Module.class)) {
                        isplugin = true;
                    }
                }
                Module module = (Module) CloudAPI.getInstance().getGuice().getInstance(cl);
                module.onLoad();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Module " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + data.getName() +
                    ConsoleColors.GRAY.getAnsiCode() +" Loaded (Author: " + data.getAuthor() + ")");
            } catch (MalformedURLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private List<ModuleData> findModuleData(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("directory has to be a directory");
        }

        List<ModuleData> moduleData = new ArrayList<>();

        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.isFile() && file.exists() && file.getName().endsWith(".jar")) {

                try (JarFile jarFile = new JarFile(file)) {
                    JarEntry entry = jarFile.getJarEntry("module.json");
                    if (entry == null) {
                        throw new FileNotFoundException("Cannot find \"module.json\" file");
                    }
                    try (InputStreamReader reader = new InputStreamReader(jarFile.getInputStream(entry))) {
                        ModuleData module = gson.fromJson(reader, ModuleData.class);
                        module.setFile(file);
                        moduleData.add(module);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return moduleData;
    }

}
