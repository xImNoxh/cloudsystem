package de.polocloud.api.module.loader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.event.base.IListener;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.IModuleHolder;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.info.ModuleTask;
import de.polocloud.api.module.info.ScheduledModuleTask;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.util.HandlerMethod;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleService implements IModuleHolder {

    private List<CloudModule> modules;
    private ModuleLoader moduleLoader;
    private File moduleDir;
    private Map<String, Map<Object, List<HandlerMethod<ModuleTask>>>> moduleTasks;

    private Map<Class<? extends CloudModule>, List<IListener>> moduleListener;
    private Map<Class<? extends CloudModule>, List<CommandListener>> moduleCommands;

    public ModuleService(File moduleDir) {
        if (PoloCloudAPI.getInstance().getType().isPlugin()) {
            return;
        }
        this.moduleDir = moduleDir;
        moduleDir.mkdirs();
        this.modules = new LinkedList<>();
        this.moduleTasks = new ConcurrentHashMap<>();

        this.moduleListener = new ConcurrentHashMap<>();
        this.moduleCommands = new ConcurrentHashMap<>();

        this.moduleLoader = new ModuleLoader(moduleDir, this);
    }

    /**
     * Returns module by name
     *
     * @param name the name of the module
     * @return driver-module or null
     */
    public CloudModule getModule(String name) {
        return this.modules.stream().filter((module -> module.info().name().equalsIgnoreCase(name))).findFirst().orElse(null);
    }

    /**
     * Registers all tasks for a {@link CloudModule}
     *
     * @param module the module
     * @param objectClass the class
     */
    public void registerModuleTasks(CloudModule module, Object objectClass) {
        List<HandlerMethod<ModuleTask>> moduleTasks = new ArrayList<>();

        for (Method m : objectClass.getClass().getDeclaredMethods()) {
            ModuleTask annotation = m.getAnnotation(ModuleTask.class);
            ScheduledModuleTask scheduledModuleTask = m.getAnnotation(ScheduledModuleTask.class);

            if (annotation != null) {
                HandlerMethod<ModuleTask> moduleTaskHandlerMethod = new HandlerMethod<>(objectClass, m, Void.class, annotation);
                if (scheduledModuleTask != null) {
                    moduleTaskHandlerMethod.setObjects(new Object[]{scheduledModuleTask});
                }
                moduleTasks.add(moduleTaskHandlerMethod);
            }
        }

        moduleTasks.sort(Comparator.comparingInt(em -> em.getAnnotation().id()));

        Map<Object, List<HandlerMethod<ModuleTask>>> listMap = this.moduleTasks.get(module.info().name());
        if (listMap == null) {
            listMap = new HashMap<>();
        }
        listMap.put(objectClass, moduleTasks);
        this.moduleTasks.put(module.info().name(), listMap);

    }

    /**
     * Calls all tasks for a {@link CloudModule}
     *
     * @param module the module
     * @param state the current state
     */
    public void callTasks(CloudModule module, ModuleState state) {
        Map<Object, List<HandlerMethod<ModuleTask>>> map = this.moduleTasks.get(module.info().name());
        if (map == null) {
            return;
        }
        map.forEach((object, handlers) -> {
            for (HandlerMethod<ModuleTask> em : handlers) {
                if (em.getObjects() != null && em.getObjects()[0] instanceof ScheduledModuleTask) {
                    ScheduledModuleTask scheduledModuleTask = (ScheduledModuleTask)em.getObjects()[0];
                    Scheduler scheduler = Scheduler.runtimeScheduler();

                    long delay = scheduledModuleTask.delay();
                    boolean sync = scheduledModuleTask.sync();
                    long repeat = scheduledModuleTask.repeat();

                    if (repeat != -1) {
                        if (sync) {
                            scheduler.schedule(() -> this.subExecute(em, state), delay, repeat);
                        } else {
                            scheduler.async().schedule(() -> this.subExecute(em, state), delay, repeat);
                        }
                    } else {
                        if (sync) {
                            scheduler.schedule(() -> this.subExecute(em, state), delay);
                        } else {
                            scheduler.async().schedule(() -> this.subExecute(em, state), delay);
                        }
                    }
                } else {
                    this.subExecute(em, state);
                }
            }
        });
    }

    private void subExecute(HandlerMethod<ModuleTask> em, ModuleState state) {

        if (em.getAnnotation().state() == state) {
            try {
                em.getMethod().invoke(em.getListener());
            } catch (IllegalAccessException | InvocationTargetException e) {
                //ignoring on shutdown
            }
        }
    }

    /**
     * Enables all modules
     */
    public void load() {
        this.moduleLoader.loadModules();
        for (CloudModule driverModule : this.modules) {
            this.callTasks(driverModule, ModuleState.STARTING);
        }
    }

    /**
     * Disables all modules
     */
    public void shutdown(Runnable runnable) {
        if (this.modules.isEmpty()) {
            runnable.run();
        }
        int count = this.modules.size();
        for (CloudModule driverModule : this.modules) {

            List<CommandListener> commandListeners = this.moduleCommands.get(driverModule.getClass());
            List<IListener> listeners = this.moduleListener.get(driverModule.getClass());

            if (listeners != null) {
                for (IListener listener : listeners) {
                    PoloCloudAPI.getInstance().getEventManager().unregisterListener(listener.getClass());
                }
            }

            if (commandListeners != null) {
                for (CommandListener commandListener : commandListeners) {
                    PoloCloudAPI.getInstance().getCommandManager().unregisterCommand(commandListener.getClass());
                }
            }


            this.callTasks(driverModule, ModuleState.STOPPING);
            count--;
            if (count <= 0) {
                runnable.run();
            }
        }
    }

    public void reload() {
        for (CloudModule driverModule : this.modules) {
            this.callTasks(driverModule, ModuleState.RELOADING);
        }
    }

    public LinkedHashMap<File, ModuleCopyType[]> getAllModulesToCopyWithInfo() {
        LinkedHashMap<File, ModuleCopyType[]> returnList = new LinkedHashMap<>();
        for (CloudModule module : modules) {
            List<ModuleCopyType> types = Arrays.asList(module.info().copyTypes());
            if (!types.contains(ModuleCopyType.NONE)) {
                returnList.put(module.getModuleFile(), module.info().copyTypes());
            }
        }
        return returnList;
    }

    public Map<Class<? extends CloudModule>, List<IListener>> getModuleListener() {
        return moduleListener;
    }

    public Map<Class<? extends CloudModule>, List<CommandListener>> getModuleCommands() {
        return moduleCommands;
    }

    public List<CloudModule> getModules() {
        return modules;
    }

    public void addModule(CloudModule module) {
        this.modules.add(module);
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public File getModuleDir() {
        return moduleDir;
    }

    public Map<String, Map<Object, List<HandlerMethod<ModuleTask>>>> getModuleTasks() {
        return moduleTasks;
    }
}
