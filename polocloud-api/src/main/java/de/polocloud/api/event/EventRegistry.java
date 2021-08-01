package de.polocloud.api.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.polocloud.api.module.Module;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventRegistry {

    private static Map<Class<? extends CloudEvent>, CopyOnWriteArrayList<EventHandler<?>>> eventMap = Maps.newConcurrentMap();
    private static Map<Module, CopyOnWriteArrayList<EventHandler<?>>> moduleEvents = Maps.newConcurrentMap();

    public static void registerModuleListener(Module module, EventHandler<?> eventHandler, Class<? extends CloudEvent> eventClass) {
        CopyOnWriteArrayList<EventHandler<?>> eventList = (moduleEvents.containsKey(module) ? moduleEvents.get(module) : Lists.newCopyOnWriteArrayList());
        eventList.add(eventHandler);
        moduleEvents.put(module, eventList);

        registerListener(eventHandler, eventClass);
    }

    public static void registerListener(EventHandler<?> eventHandler, Class<? extends CloudEvent> eventClass) {
        CopyOnWriteArrayList<EventHandler<?>> eventList = (eventMap.containsKey(eventClass) ? eventMap.get(eventClass) : Lists.newCopyOnWriteArrayList());
        eventList.add(eventHandler);

        eventMap.put(eventClass, eventList);
    }

    public static void unregisterModuleListener(Module module) {
        if (!moduleEvents.containsKey(module)) return;

        for (EventHandler<?> moduleEvents : moduleEvents.get(module)) {
            for (Class<? extends CloudEvent> key : eventMap.keySet()) {
                CopyOnWriteArrayList<EventHandler<?>> events = eventMap.get(key);
                for (EventHandler<?> event : events) {
                    if (moduleEvents.equals(event)) events.remove(event);
                }
                eventMap.put(key, events);
            }
        }
        moduleEvents.remove(module);
    }

    public static void fireEvent(CloudEvent event) {
        if (eventMap.containsKey(event.getClass())) {
            CopyOnWriteArrayList<EventHandler<?>> eventHandlers = eventMap.get(event.getClass());
            for (EventHandler eventHandler : eventHandlers) {
                eventHandler.handleEvent(event);
            }
        }
    }
}
