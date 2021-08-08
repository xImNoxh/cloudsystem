package de.polocloud.permission.api.database.scanner;

import de.polocloud.permission.api.database.adapter.DatabaseService;
import de.polocloud.permission.api.database.annotations.Column;
import de.polocloud.permission.api.database.annotations.Entity;
import de.polocloud.permission.api.database.annotations.PrimaryKey;
import de.polocloud.permission.api.database.annotations.Repository;
import de.polocloud.permission.api.database.pool.RepositoryPool;
import de.polocloud.permission.api.database.pool.SqlRepository;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class RepositoryProcessor {

    private final DatabaseService databaseService;
    private final ClassLoader classLoader;

    public RepositoryProcessor(ClassLoader classLoader, DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.classLoader = classLoader;

        List<Class<?>> classes = null;
        try {
            classes = ClassHelper.findClassesWithAnnotation(classLoader, "de.polocloud", Repository.class);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        for (Class<?> aClass : classes) {
            try {
                RepositoryPool.list.add(instantiateRepository(aClass));
                System.out.println("[Scanner] Found repository: " + aClass.getSimpleName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private <T extends SqlRepository<?, ?>> Object instantiateRepository(Class<?> repository) throws ClassNotFoundException {
        Type[] genericTypes = null;

        for (Type genericInterface : repository.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
            }
        }

        Class entity = Class.forName(genericTypes[0].getTypeName());

        Class[] interfaces = new Class[]{repository};

        Object object = Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, (proxy, method, args) -> {

            String name = method.getName().toLowerCase();

            if (name.equals("equals")) {
                Class<? extends SqlRepository<?, ?>> valueClass = (Class<? extends SqlRepository<?, ?>>) args[0];
                return valueClass.equals(repository);
            }

            if (name.contains("find")) {
                name = name.replace("find", "");

                if (name.contains("all")) {
                    String sql = getSqlNameFromClassName(entity);

                    String primaryKey = null;

                    Field[] fields = entity.isInterface() ? ClassHelper.findClassImplementing(classLoader, entity).getDeclaredFields() : entity.getDeclaredFields();

                    for (Field field : fields) {
                        if (!field.isAnnotationPresent(PrimaryKey.class)) continue;
                        primaryKey = field.getName();
                    }

                    List<Object> list = databaseService.get().getListFromTable(sql, primaryKey);

                    List entities = new CopyOnWriteArrayList();

                    for (Object o : list) {
                        entities.add(findEntity(entity, primaryKey, o));
                    }
                    return list;
                }

                if (name.contains("by")) {
                    name = name.replace("by", "");
                    return findEntity(entity, name, args[0]);
                }
            }

            if (name.contains("remove")) {
                name = name.replace("remove", "");
                if (name.contains("by")) {
                    name = name.replace("by", "");
                    return deleteEntity(entity, name, args[0]);
                }
            }

            if (name.contains("create")) {

                Field[] fields = entity.isInterface() ? ClassHelper.findClassImplementing(classLoader, entity).getDeclaredFields() : entity.getDeclaredFields();

                String primaryKey = null;
                for (Field field : fields) {
                    if (!field.isAnnotationPresent(PrimaryKey.class)) continue;
                    primaryKey = field.getName();
                }

                Field field = args[0].getClass().getDeclaredField(primaryKey);
                field.setAccessible(true);

                if (findEntity(entity, primaryKey, field.get(args[0])) != null) {
                    return true;
                }

                return createEntity(entity, args[0]);
            }

            if (name.contains("save")) {
                return saveEntityClass(entity, args[0]);
            }

            throw new RuntimeException(name + " -> no method found");
        });

        return object;
    }

    private Object findEntity(Class entityClass, String name, Object obj) throws InstantiationException, IllegalAccessException {
        String table = getSqlNameFromClassName(entityClass);

        Object objectInstance;

        if (entityClass.isInterface())
            objectInstance = ClassHelper.findClassImplementing(classLoader, entityClass).newInstance();
        else objectInstance = entityClass.newInstance();

        if (name.equals("id")) {
            Class entity = entityClass;
            if (entityClass.isInterface()) entity = ClassHelper.findClassImplementing(classLoader, entityClass);

            for (Field declaredField : entity.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Column.class)) {
                    declaredField.setAccessible(true);
                    if (declaredField.isAnnotationPresent(PrimaryKey.class)) {
                        name = declaredField.getName();
                    }
                }
            }
        }

        boolean exists = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", ResultSet::next, false);

        if (!exists) return null;

        for (Field declaredField : objectInstance.getClass().getDeclaredFields()) {
            if (!declaredField.isAnnotationPresent(Column.class)) continue;
            declaredField.setAccessible(true);

            switch (declaredField.getType().getSimpleName().toLowerCase()) {
                case "string":
                    String s = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getString(declaredField.getName()) : "", "");
                    declaredField.set(objectInstance, s);
                    break;
                case "boolean":
                    boolean b = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() && resultSet.getBoolean(declaredField.getName()), false);
                    declaredField.set(objectInstance, b);
                    break;
                case "int":
                case "integer":
                    int i = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getInt(declaredField.getName()) : -1, -1);
                    declaredField.set(objectInstance, i);
                    break;
                case "long":
                    Long l = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getLong(declaredField.getName()) : -1L, -1L);
                    declaredField.set(objectInstance, l);
                    break;
                case "float":
                    Float f = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getFloat(declaredField.getName()) : -1F, -1F);
                    declaredField.set(objectInstance, f);
                    break;
                case "double":
                    Double d = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getDouble(declaredField.getName()) : -1D, -1D);
                    declaredField.set(objectInstance, d);
                    break;
                case "uuid":
                    UUID u = UUID.fromString(databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getString(declaredField.getName()) : "", ""));
                    declaredField.set(objectInstance, u);
                    break;
                case "byte[]":
                    byte[] bytes = databaseService.executor().executeQuery("SELECT * FROM " + table + " WHERE " + name.toLowerCase() + "= '" + obj + "'", resultSet -> resultSet.next() ? resultSet.getBytes(declaredField.getName()) : new byte[0], new byte[0]);
                    declaredField.set(objectInstance, bytes);
                    break;
            }
        }

        return objectInstance;
    }

    private boolean deleteEntity(Class entityClass, String name, Object obj) {
        databaseService.executor().executeUpdate("DELETE FROM TABLE " + getSqlNameFromClassName(entityClass) + " WHERE " + name.toLowerCase() + "= '" + obj + "'");
        return true;
    }

    private boolean createEntity(Class entityClass, Object obj) {

        String primaryKey = null;
        List<String> columns = new CopyOnWriteArrayList<>();

        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                columns.add(declaredField.getName());
                if (declaredField.isAnnotationPresent(PrimaryKey.class)) primaryKey = declaredField.getName();
            }
        }

        Field primaryField = null;
        try {
            primaryField = obj.getClass().getDeclaredField(primaryKey);
            primaryField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            if (findEntity(entityClass, primaryKey, primaryField.get(obj).toString()) != null) return false;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        List<String> values = new CopyOnWriteArrayList<>();

        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                declaredField.setAccessible(true);
                try {
                    if (declaredField.getType().getSimpleName().equalsIgnoreCase("boolean")) {
                        if ((Boolean) declaredField.get(obj)) values.add("1");
                        else values.add("0");
                    } else {
                        values.add(declaredField.get(obj).toString());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder("INSERT INTO " + getSqlNameFromClassName(entityClass) + " (");

        for (String column : columns) {
            stringBuilder.append(column);
            if (!columns.get(columns.size() - 1).equals(column)) stringBuilder.append(", ");
            else stringBuilder.append(") VALUES ('");
        }

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            stringBuilder.append(value);
            if (i == values.size() - 1) stringBuilder.append("')");
            else stringBuilder.append("', '");
        }

        databaseService.executor().executeUpdate(stringBuilder.toString());

        return true;
    }

    private boolean saveEntityClass(Class entityClass, Object obj) {

        List<String> columns = new CopyOnWriteArrayList<>();

        String primaryKey = null;

        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                if (declaredField.isAnnotationPresent(PrimaryKey.class)) {
                    primaryKey = declaredField.getName();
                } else {
                    columns.add(declaredField.getName());
                }
            }
        }

        List<String> values = new CopyOnWriteArrayList<>();
        String primaryValue = null;

        for (Field declaredField : obj.getClass().getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Column.class)) {
                declaredField.setAccessible(true);
                if (declaredField.isAnnotationPresent(PrimaryKey.class)) {
                    try {
                        if (declaredField.getType().getSimpleName().equalsIgnoreCase("boolean")) {
                            if ((Boolean) declaredField.get(obj)) primaryValue = "1";
                            else primaryValue = "0";
                        } else {
                            primaryValue = declaredField.get(obj).toString();
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        if (declaredField.getType().getSimpleName().equalsIgnoreCase("boolean")) {
                            if ((Boolean) declaredField.get(obj)) values.add("1");
                            else values.add("0");
                        } else {
                            values.add(declaredField.get(obj).toString());
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (int i = 0; i < columns.size(); i++) {
            databaseService.executor().executeUpdate("UPDATE " + getSqlNameFromClassName(entityClass) + " SET " + columns.get(i) + "= '" + values.get(i) + "' WHERE " + primaryKey + "= '" + primaryValue + "'");
        }

        return true;
    }

    private String getSqlNameFromClassName(Class clazz) {
        if (clazz.isAnnotationPresent(Entity.class)) {
            Annotation annotation = clazz.getAnnotation(Entity.class);
            String value = null;
            try {
                value = annotation.getClass().getMethod("value").invoke(annotation).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (!value.isEmpty()) return value.toLowerCase();
        }

        String name = clazz.getSimpleName().toLowerCase();
        name = name.replace("impl", "");
        name = name + "s";

        return name;
    }


}
