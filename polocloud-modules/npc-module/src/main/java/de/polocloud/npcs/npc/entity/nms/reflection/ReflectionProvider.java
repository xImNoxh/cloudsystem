package de.polocloud.npcs.npc.entity.nms.reflection;

import java.io.EOFException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionProvider {

    public static <T> Object invokeClassMethod(Class<T> clazz, Object instance, String methodName, Class<?>[] typeArgs, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, typeArgs);
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof EOFException))
                System.err.println("Error while invoking method: " + methodName + ", class: " + clazz.getName());
        } catch (Exception e) {
            System.err.println("Error while invoking method: " + methodName + ", class: " + clazz.getName());
        }
        return null;
    }

    public static <T> T getNewInstanceOfClass(Class<T> clazz, Class<?>[] typeArgs, Object... args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(typeArgs);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof EOFException))
                System.err.println("Error while creating new Instance of class: " + clazz.getName());
        } catch (Exception e) {
            System.err.println("Error while creating new Instance of class: " + clazz.getName());
        }
        return null;
    }
}
