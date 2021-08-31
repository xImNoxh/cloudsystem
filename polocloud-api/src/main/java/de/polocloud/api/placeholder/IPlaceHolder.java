package de.polocloud.api.placeholder;

public interface IPlaceHolder<V> {

    /**
     * Applies the placeholder
     *
     * @param v the object
     * @param input the input string
     */
    String apply(V v, String input);

    /**
     * ALl trigger classes
     */
    Class<?>[] getAcceptedClasses();
}
