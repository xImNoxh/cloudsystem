package de.polocloud.api.common;

/**
 * A consumer which returns a given value
 * and throws an {@link Exception}
 *
 * @param <T> the parameter-object-generic
 */
public interface ExceptionSupplier<T> {

    /**
     * Consumes this consumer with a given object
     *
     * @return the t-type object
     */
    T supply() throws Exception;
}
