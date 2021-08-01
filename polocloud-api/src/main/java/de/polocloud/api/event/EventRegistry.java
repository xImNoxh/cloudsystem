package de.polocloud.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventRegistry {

    public static Map<Class<? extends CloudEvent>, List<EventHandler<?>>> eventMap = new ConcurrentHashMap<>();

    public static void registerListener(EventHandler<?> eventHandler, Class<? extends CloudEvent> eventClass) {
        List<EventHandler<?>> eventList;

        if (eventMap.containsKey(eventClass)) {
            eventList = eventMap.get(eventClass);
        } else {
            eventList = new ArrayList<>();
        }

        eventList.add(eventHandler);

        eventMap.put(eventClass, eventList);
    }

    public static void fireEvent(CloudEvent event) {
        if (eventMap.containsKey(event.getClass())) {
            List<EventHandler<?>> eventHandlers = eventMap.get(event.getClass());
            for (EventHandler eventHandler : eventHandlers) {
                eventHandler.handleEvent(event);
            }
        }
    }
}
