package de.polocloud.api.module.loader;

import com.google.gson.JsonObject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.info.ModuleInfo;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.util.gson.PoloHelper;

import java.io.File;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleLoader {

    /**
     * The directory where modules are
     */
    private final File modulesDir;

    /**
     * The module service
     */
    private final ModuleService moduleService;

    public ModuleLoader(File modulesDir, ModuleService moduleService) {
        this.modulesDir = modulesDir;
        this.moduleService = moduleService;
    }


    /**
     * Ignoring al files that don't end with ".jar" (folders etc)
     *
     * @return amount of modules
     */
    public int getSize() {
        int i = 0;
        for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
            if (!file.isFile()) {
                continue;
            }
            if (file.getName().endsWith(".jar")) {
                i++;
            }
        }
        return i;
    }

    /**
     * Loads all modules
     */
    public void loadModules() {
        try {
            int size = this.getSize();
            if (size == 0) {
                PoloLogger.print(LogLevel.INFO, "§7There are currently §bno modules §7to be loaded!");
            } else {
                PoloLogger.print(LogLevel.INFO, "§7There " + (size == 1 ? "is" : "are") + " §b" + size + " §7Cloud-Modules to load and enable!");
                for (File file : Objects.requireNonNull(this.modulesDir.listFiles())) {
                    if (file.getName().endsWith(".jar")) {

                        ModuleHelper moduleHelper = new ModuleHelper(file);
                        Class<?> loadedClass;
                        URLClassLoader urlClassLoader = moduleHelper.classLoader();
                        JsonObject jsonObject = moduleHelper.loadJson("module.json");

                        List<IListener> listeners = new LinkedList<>();
                        List<CommandListener> commandListeners = new LinkedList<>();
                        CloudModule cloudModule = null;


                        if (jsonObject == null) {
                            JarFile jarFile = new JarFile(file);
                            Enumeration<JarEntry> e = jarFile.entries();

                            while (e.hasMoreElements()) {
                                JarEntry jarEntry = e.nextElement();
                                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                                    continue;
                                }
                                String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                                className = className.replace('/', '.');

                                loadedClass = urlClassLoader.loadClass(className);
                                if (CloudModule.class.isAssignableFrom(loadedClass)) {
                                    if (loadedClass.isAnnotationPresent(ModuleInfo.class)) {
                                        cloudModule = (CloudModule) loadedClass.newInstance();
                                    } else {
                                        PoloLogger.print(LogLevel.INFO, "§cThe class §e" + loadedClass.getName() + " §cof the Module §e" + file.getName() + " §cdoesn't have a §e@" + ModuleInfo.class.getSimpleName() + "-Annotation!");
                                    }
                                } else if (IListener.class.isAssignableFrom(loadedClass)) {

                                    IListener instance = (IListener) PoloHelper.getInstance(loadedClass);
                                    listeners.add(instance);

                                } else if (CommandListener.class.isAssignableFrom(loadedClass)) {
                                    CommandListener instance = (CommandListener) PoloHelper.getInstance(loadedClass);
                                    commandListeners.add(instance);
                                }

                            }
                        } else {
                            String main = jsonObject.get("main").getAsString();
                            try {
                                Class<? extends CloudModule> mainClass = (Class<? extends CloudModule>) urlClassLoader.loadClass(main);
                                cloudModule = mainClass.newInstance();
                            } catch (ClassNotFoundException e) {
                                PoloLogger.print(LogLevel.ERROR, "§cCouldn't find main class §e" + main + "§c! Or maybe it doesn't extend the Module-Class ?");
                            }
                        }

                        if (cloudModule == null) {
                            PoloLogger.print(LogLevel.ERROR, "§cNo Class extending §e" + CloudModule.class.getSimpleName() + " §cwas found in file §e" + file.getName() + "§c!");
                            return;
                        }

                        for (IListener listener : listeners) {
                            PoloCloudAPI.getInstance().getEventManager().registerListener(listener);
                            List<IListener> list = moduleService.getModuleListener().getOrDefault(cloudModule.getClass(), new LinkedList<>());
                            list.add(listener);
                            moduleService.getModuleListener().put(cloudModule.getClass(), list);
                        }

                        for (CommandListener commandListener : commandListeners) {
                            PoloCloudAPI.getInstance().getCommandManager().registerCommand(commandListener);
                            List<CommandListener> list = moduleService.getModuleCommands().getOrDefault(cloudModule.getClass(), new LinkedList<>());
                            list.add(commandListener);
                            moduleService.getModuleCommands().put(cloudModule.getClass(), list);
                        }

                        ModuleInfo info = cloudModule.info();

                        File dataDir = new File(FileConstants.MASTER_MODULES, cloudModule.info().name() + "/");
                        dataDir.mkdirs();
                        cloudModule.setDataDirectory(dataDir);
                        cloudModule.setModuleFile(file);

                        if (hasSpaces(cloudModule.info().name())) {
                            PoloLogger.print(LogLevel.ERROR, "§cCloudModule-Names may not contain any §espaces§c! Please fix this and try to load the module again");
                            continue;
                        }
                        moduleService.registerModuleTasks(cloudModule, cloudModule);
                        moduleService.callTasks(cloudModule, ModuleState.LOADING);

                        moduleService.addModule(cloudModule);
                        PoloLogger.print(LogLevel.INFO, "§7The Cloud-Module §b" + info.name() + " §7[§7Author§b: " + Arrays.toString(info.authors()) + " §7| Version§b: " + info.version() + " §7| Copy§b: " + Arrays.toString(info.copyTypes()) + "§7] §7was loaded!");

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasSpaces(String s) {
        int l = s.length();
        boolean b = false;
        for (int i = 0; i < l; i++) {
            char c = s.charAt(i);
            b = (c == ' ');
        }
        return b;
    }

}
