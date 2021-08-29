package de.polocloud.api.util;

public interface Acceptable<T> {

    /**
     * Checks if this request is accepted
     * to return with a provided parameter object
     *
     * @param t the object
     * @return request if allowed
     */
    boolean isAccepted(T t);
}
