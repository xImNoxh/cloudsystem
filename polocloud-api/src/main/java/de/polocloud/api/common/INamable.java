package de.polocloud.api.common;

/**
 * This interface marks that the object
 * that implements has a name and can be called through the {@link INamable#getName()} method
 */
public interface INamable {

    /**
     * The name of this object
     */
    String getName();

}
