package de.polocloud.api.pool;

import de.polocloud.api.common.INamable;
import de.polocloud.api.util.gson.PoloHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public interface PoloObject<V> extends Serializable, INamable {

    /**
     * The snowflake of this object
     */
    long getSnowflake();

    default void scanForNulls(Consumer<Field> nullFound) {
        PoloHelper.sneakyThrows(() -> {
            for (Field declaredField : this.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                if (declaredField.get(this) == null) {
                    nullFound.accept(declaredField);
                }
            }
        });
    }

}
