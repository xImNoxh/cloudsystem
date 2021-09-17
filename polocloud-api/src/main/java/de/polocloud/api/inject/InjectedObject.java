package de.polocloud.api.inject;

/**
 * Just a class to automatically fill all fields
 * that are marked with {@link Inject} annotation
 * It just refers to {@link PoloInject#injectFields(Object, Class)}
 * but works with default constructor
 */
public abstract class InjectedObject {

    protected InjectedObject() {
        PoloInject.injectFields(this, getClass());
    }
}
