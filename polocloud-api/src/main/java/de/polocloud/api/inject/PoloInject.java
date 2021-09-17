package de.polocloud.api.inject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PoloInject {

    //The cached values
    public static final Map<Class<?>, Object> REGISTER_INJECTORS;
    public static final Map<String, Object> NAMED_INJECTORS;
    public static final Map<String, Class<?>> NAMED_CLASS_INJECTORS;

    static {
        REGISTER_INJECTORS = new HashMap<>();
        NAMED_INJECTORS = new HashMap<>();
        NAMED_CLASS_INJECTORS = new HashMap<>();
    }

    /**
     * Binds a class to something
     *
     * @param cls the class
     * @param <T> the generic
     * @return binder object
     */
    public static <T> InstanceBinder<T> bind(Class<T> cls) {
        return new InstanceBinder<>(cls);
    }


    /**
     * Sets all Fields for a given class object
     *
     * @param instance the class instance
     * @param cls the class to get all fields of
     */
    public static void injectFields(Object instance, Class<?> cls) {

        for (Field declaredField : cls.getDeclaredFields()) {
            declaredField.setAccessible(true);
            Inject annotation = declaredField.getAnnotation(Inject.class);
            if (annotation != null && !annotation.value().trim().isEmpty()) {

                Object obj = NAMED_INJECTORS.get(annotation.value());
                Class<?> instanceClass = obj == null ? declaredField.getType() : NAMED_CLASS_INJECTORS.get(annotation.value());

                try {
                    declaredField.set(instance, PoloInject.getInstance(instanceClass));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (declaredField.getAnnotation(Inject.class) != null) {
                Class<?> type = declaredField.getType();
                try {
                    declaredField.set(instance, PoloInject.getInstance(type));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets an instance for a given class if registered
     * Is it's not registered an {@link IllegalStateException} will be thrown
     *
     * @param cls the class to get the instance of
     * @param <T> the generic
     * @return object or null
     */
    public static <T> T getInstance(Class<T> cls) {

        Object object = REGISTER_INJECTORS.get(cls);
        if (object != null) {
            injectFields(object, cls);
            return (T) object;
        }
        throw new IllegalStateException("Tried to get instance for " + cls + " but was not registered!");
    }

}
