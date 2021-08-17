package de.polocloud.api.network.request.base.component;

import de.polocloud.api.network.request.base.future.PoloFuture;
import de.polocloud.api.config.JsonData;

public interface PoloComponent<T> {

    static <V> PoloComponent<V> request(Class<V> vClass) {
        return new SimpleComponent<>();
    }

    /**
     * The document data
     */
    JsonData getDocument();

    /**
     * Creates a response for this request
     *
     * @param vClass the class type
     * @param <V> the gener c
     * @return response
     */
    <V> PoloComponent<V> createResponse(Class<V> vClass);

    /**
     * If this component is a response to something
     */
    boolean isResponse();

    /**
     * The generic-type-class
     *
     * @return class
     */
    Class<T> typeClass();

    /**
     * Sets the type class
     * @param typeClass the class
     * @return current request
     */
    PoloComponent<T> typeClass(Class<?> typeClass);

    /**
     * The id of the request of the response
     *
     * @return string-id
     */
    String getId();

    /**
     * The target of this component
     */
    String getTarget();

    /**
     * Gets the data of this response
     *
     * @return data
     */
    T getData();

    /**
     * Queries this request
     */
    PoloFuture<T> query();

    /**
     * Gets the error of this response if not null
     *
     * @return exception
     */
    Throwable getException();

    /**
     * The time this component took
     *
     * @return long ms
     */
    long getCompletionTimeMillis();

    /**
     * The key identifier of this component
     */
    String getKey();

    /**
     * If component was successful
     */
    boolean isSuccess();

    /**
     * Sets the id of this response
     *
     * @param id the id
     * @return current response
     */
    PoloComponent<T> id(String id);

    /**
     * Sets the key of this response
     *
     * @param key the key
     * @return current response
     */
    PoloComponent<T> key(String key);

    /**
     * Sets the document of this response
     *
     * @param document the document
     * @return current response
     */
    PoloComponent<T> document(JsonData document);

    /**
     * Sets the data of this component
     * @param t the data
     * @return current component
     */
    PoloComponent<T> value(T t);

    /**
     * Sets the error of this response
     *
     * @param throwable the error
     * @return current response
     */
    PoloComponent<T> exception(Throwable throwable);

    /**
     * Sets the target of this response
     *
     * @param target the target
     * @return current response
     */
    PoloComponent<T> target(String target);

    /**
     * Sets the success-state of this response
     *
     * @param success the state
     * @return current response
     */
    PoloComponent<T> success(boolean success);

    /**
     * Sends this response
     */
    void respond();
}
