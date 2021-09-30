package de.polocloud.api.util.other;

import de.polocloud.api.util.PoloHelper;

public class WrappedObject<T> {

    /**
     * The json data string
     */
    private final String jsonString;

    /**
     * The name of the object class
     */
    private final String className;

    public WrappedObject(T obj) {
        this(PoloHelper.GSON_INSTANCE.toJson(obj), obj == null ? "null" : obj.getClass().getName());
    }

    public WrappedObject(String jsonString, String className) {
        this.jsonString = jsonString;
        this.className = className;
    }

    /**
     * Unwraps the {@link String} to the given class object
     *
     * @param tClass the class
     * @return object or exception
     */
    public T unwrap(Class<T> tClass) {
        return PoloHelper.GSON_INSTANCE.fromJson(this.jsonString, tClass);
    }

    @Override
    public String toString() {
        return this.jsonString;
    }

    /**
     * Unwraps the object if the {@link Class} exists on the received instance
     * If not the object will be returned null
     *
     * @throws ClassNotFoundException if the class that was transferred does not exist
     */
    @Deprecated
    public T unwrap() throws ClassNotFoundException {
        Class<T> tClass = (Class<T>) Class.forName(className);
        return this.unwrap(tClass);
    }
}
