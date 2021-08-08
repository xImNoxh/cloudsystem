package de.polocloud.permission.api.database.scanner;

import de.polocloud.permission.api.database.adapter.DatabaseService;
import de.polocloud.permission.api.database.annotations.Column;
import de.polocloud.permission.api.database.annotations.Entity;
import de.polocloud.permission.api.database.annotations.PrimaryKey;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

public class EntityProcessor {


    private final DatabaseService databaseService;

    public EntityProcessor(ClassLoader classLoader, DatabaseService databaseService) {
        this.databaseService = databaseService;

        List<Class<?>> classes = null;
        try {
            classes = ClassHelper.findClassesWithAnnotation(classLoader, "de.bytemc", Entity.class);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        for (Class<?> aClass : classes) {
            try {
                initEntity(aClass);
                System.out.println("[Scanner] Found entity: " + aClass.getSimpleName());
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void initEntity(Class<?> entity) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");

        String name = entity.getSimpleName();

        Annotation annotation = entity.getAnnotation(Entity.class);
        String value = annotation.getClass().getMethod("value").invoke(annotation).toString();

        if (value.isEmpty()) {
            name = name.toLowerCase();
            name = name.replace("impl", "");
            name = name + "s";
        } else {
            name = value;
        }

        stringBuilder.append(name).append(" (");

        String primaryKey = null;

        for (Field declaredField : entity.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                if (declaredField.isAnnotationPresent(PrimaryKey.class)) {
                    primaryKey = declaredField.getName();
                }

                stringBuilder.append(declaredField.getName().toLowerCase()).append(" ").append(getSqlTypeFromClassType(declaredField.getType())).append(", ");
            }
        }

        if (primaryKey != null) {
            stringBuilder.append("PRIMARY KEY (").append(primaryKey).append(")").append(")");
        }

        databaseService.executor().executeUpdate(stringBuilder.toString());
    }

    private String getSqlTypeFromClassType(Class<?> clazz) {
        switch (clazz.getSimpleName()) {
            case "UUID":
                return "VARCHAR(64)";
            case "Integer":
            case "int":
                return "INT";
            case "Long":
            case "long":
                return "BIGINT";
            case "Float":
            case "float":
                return "FLOAT";
            case "Double":
            case "double":
                return "DOUBLE";
            case "boolean":
            case "Boolean":
                return "BOOLEAN";
            case "byte[]":
                return "BLOB";
            default:
                return "TEXT";
        }
    }


}
