package de.polocloud.api.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.polocloud.api.module.CloudModule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventRegistry {

    /**
     * The normal registered {@link EventHandler}s
     */
    private static final Map<Class<? extends CloudEvent>, CopyOnWriteArrayList<EventHandler<?>>> eventMap;

    /**
     * The module registered {@link EventHandler}s
     */
    private static final Map<CloudModule, CopyOnWriteArrayList<EventHandler<?>>> moduleEvents;

    static {
        eventMap = new HashMap<>();
        moduleEvents = new HashMap<>();
    }

    /**
     * Registers {@link EventHandler} for a given event from a {@link CloudModule}
     * @param module the module
     * @param eventHandler the handler
     * @param eventClass the class of the event
     */
    public static void registerModuleListener(CloudModule module, EventHandler<?> eventHandler, Class<? extends CloudEvent> eventClass) {
        CopyOnWriteArrayList<EventHandler<?>> eventList = (moduleEvents.containsKey(module) ? moduleEvents.get(module) : Lists.newCopyOnWriteArrayList());
        eventList.add(eventHandler);
        moduleEvents.put(module, eventList);

        registerListener(eventHandler, eventClass);
    }

    /**
     * Registers a {@link EventHandler} for a specific {@link CloudEvent}
     *
     * @param eventHandler the handler
     * @param eventClass the class of the event
     */
    public static void registerListener(EventHandler<?> eventHandler, Class<? extends CloudEvent> eventClass) {
        CopyOnWriteArrayList<EventHandler<?>> eventList = (eventMap.containsKey(eventClass) ? eventMap.get(eventClass) : Lists.newCopyOnWriteArrayList());
        eventList.add(eventHandler);

        eventMap.put(eventClass, eventList);
    }

    /**
     * Unregisters all listeners from a {@link CloudModule}
     *
     * @param module the module to unregister the listeners for
     */
    public static void unregisterModuleListener(CloudModule module) {
        if (!moduleEvents.containsKey(module)) return;

        for (EventHandler<?> moduleEvents : moduleEvents.get(module)) {
            for (Class<? extends CloudEvent> key : eventMap.keySet()) {
                CopyOnWriteArrayList<EventHandler<?>> events = eventMap.get(key);
                events.removeIf(moduleEvents::equals);
                eventMap.put(key, events);
            }
        }
        moduleEvents.remove(module);
    }

    /**
     * Fires a {@link CloudEvent} and handles all {@link EventHandler}
     *
     * @param event the event to fire
     */
    public static void fireEvent(CloudEvent event) {
        if (eventMap.containsKey(event.getClass())) {
            CopyOnWriteArrayList<EventHandler<?>> eventHandlers = eventMap.get(event.getClass());
            for (EventHandler eventHandler : eventHandlers) {
                eventHandler.handleEvent(event);
            }
        }
    }
}
