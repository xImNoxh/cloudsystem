package de.polocloud.api.inject.feature;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.inject.SimpleInjector;
import de.polocloud.api.inject.annotation.Inject;

/**
 * Just a class to automatically fill all fields
 * that are marked with {@link Inject} annotation
 * It just refers to {@link SimpleInjector#injectFields(Object, Class)}
 * but works with default constructor
 */
public abstract class InjectedObject {

    protected InjectedObject() {
        ((SimpleInjector) PoloCloudAPI.getInstance().getInjector()).injectFields(this, getClass());
    }
}
