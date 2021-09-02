package de.polocloud.api.util;

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
        this.jsonString = PoloHelper.GSON_INSTANCE.toJson(obj);
        this.className = obj.getClass().getName();
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

    /**
     * Unwraps the object if the {@link Class} exists on the received instance
     * If not the object will be returned null
     */
    public T unwrap() {
        try {
            Class<T> tClass = (Class<T>) Class.forName(className);
            return this.unwrap(tClass);
        } catch (ClassNotFoundException e) {
            //Ignoring class not found
        }
        return null;
    }
}
