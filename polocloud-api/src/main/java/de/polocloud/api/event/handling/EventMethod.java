
package de.polocloud.api.event.handling;

import java.lang.reflect.Method;

/**
 * Used to store Annotations for
 *  and {@link EventHandler}
 * @param <T>
 */
public class EventMethod<T> {

    private final Object listener;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

    private Object[] objects;

    public EventMethod(Object listener, Method method, Class<?> aClass, T annotation) {
        this.listener = listener;
        this.method = method;
        this.aClass = aClass;
        this.annotation = annotation;
    }

    public void setObjects(Object[] objects) {
        this.objects = objects;
    }

    public Object getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public T getAnnotation() {
        return annotation;
    }

    public Object[] getObjects() {
        return objects;
    }
}
