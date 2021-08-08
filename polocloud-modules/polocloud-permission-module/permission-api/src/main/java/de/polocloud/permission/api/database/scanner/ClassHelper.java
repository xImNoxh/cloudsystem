package de.polocloud.permission.api.database.scanner;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class ClassHelper {

    public static List<Class<?>> findClasses(ClassLoader classLoader, String packageName) throws IOException, URISyntaxException {
        String packagePath = packageName.replace('.', '/');
        URI packageURI = Objects.requireNonNull(classLoader.getResource(packagePath)).toURI();
        ArrayList<Class<?>> allClasses = new ArrayList<>();

        Path root;
        if (packageURI.toString().startsWith("jar:")) {
            try {
                root = FileSystems.getFileSystem(packageURI).getPath(packagePath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(packageURI, Collections.emptyMap()).getPath(packagePath);
            }
        } else {
            root = Paths.get(packageURI);
        }

        String extension = ".class";
        try (final Stream<Path> allPaths = Files.walk(root)) {
            allPaths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String path = file.toString().replace(File.separator, ".");
                    String name = path.substring(path.indexOf(packageName), path.length() - extension.length());
                    Class clazz = Class.forName(name);
                    allClasses.add(clazz);
                } catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) { }
            });
        }
        return allClasses;
    }

    public static List<Class<?>> findClassesWithAnnotation(ClassLoader classLoader, String packageName, Class<? extends Annotation> annotation) throws IOException, URISyntaxException {
        String packagePath = packageName.replace('.', '/');
        URI packageURI = Objects.requireNonNull(classLoader.getResource(packagePath)).toURI();
        ArrayList<Class<?>> allClasses = new ArrayList<>();

        Path root;
        if (packageURI.toString().startsWith("jar:")) {
            try {
                root = FileSystems.getFileSystem(packageURI).getPath(packagePath);
            } catch (final FileSystemNotFoundException e) {
                root = FileSystems.newFileSystem(packageURI, Collections.emptyMap()).getPath(packagePath);
            }
        } else {
            root = Paths.get(packageURI);
        }

        String extension = ".class";
        try (final Stream<Path> allPaths = Files.walk(root)) {
            allPaths.filter(Files::isRegularFile).forEach(file -> {
                try {
                    String path = file.toString().replace(File.separator, ".");
                    String name = path.substring(path.indexOf(packageName), path.length() - extension.length());
                    Class clazz = Class.forName(name);
                    if (clazz.isAnnotationPresent(annotation)) allClasses.add(clazz);
                } catch (final ClassNotFoundException | StringIndexOutOfBoundsException ignored) {}
            });
        }
        return allClasses;
    }

    public static Class<?> findClassImplementing(ClassLoader classLoader, Class clazz) {
        try {
            List<Class<?>> classes = findClasses(classLoader, "net.theVance");

            for (Class<?> aClass : classes) {
                for (Class<?> anInterface : aClass.getInterfaces()) {
                    if(anInterface.equals(clazz)) return aClass;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }


}
