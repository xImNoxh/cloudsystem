package de.polocloud.api.guice.own;

/**
 * Just a class to automatically fill all fields
 * that are marked with {@link Inject} annotation
 * It just refers to {@link Guice#injectFields(Object, Class)}
 * but works with default constructor
 */
public abstract class GuiceObject {

    protected GuiceObject() {
        Guice.injectFields(this, getClass());
    }
}
