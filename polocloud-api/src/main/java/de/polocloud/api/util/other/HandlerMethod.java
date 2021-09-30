
package de.polocloud.api.util.other;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@AllArgsConstructor @Getter @Setter
public class HandlerMethod<T> {

    private final Object listener;
    private final Method method;
    private final Class<?> aClass;
    private final T annotation;

    private Object[] objects;

}
