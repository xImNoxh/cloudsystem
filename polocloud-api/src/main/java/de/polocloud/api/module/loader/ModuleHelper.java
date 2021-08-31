package de.polocloud.api.module.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.polocloud.api.PoloCloudAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ModuleHelper {

    /**
     * The file (mostly a jar file)
     */
    private final File file;

    public ModuleHelper(File file) {
        this.file = file;
    }

    /**
     * Used for {loadJson}
     *
     * @param filename the name of the file to load
     * @return content of provided file
     */
    public String loadFile(String filename) {
        try {
            JarFile jf = new JarFile(this.file);
            JarEntry je = jf.getJarEntry(filename);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(jf.getInputStream(je)))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                jf.close();
                br.close();
                return builder.toString();
            } catch (Exception e) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Loads a {@link JsonObject} from a File
     *
     * @param filename the name of the file
     * @return JsonObject from File content
     */
    public JsonObject loadJson(String filename) {
        String file = this.loadFile(filename);
        if (file == null) {
            return new JsonObject();
        }
        return new JsonParser().parse(file).getAsJsonObject();
    }

    public Class<?> loadClass(String name) {
        try {
            return Class.forName(name, true, classLoader());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads the class loader
     * @return the URLClassLoder from the loaded class
     */
    public URLClassLoader classLoader() {
        try {
            return URLClassLoader.newInstance(new URL[]{ new URL("jar:file:" + file.toPath() +"!/") });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Finds a class
     *
     * @param name the name of the class
     * @return class by name
     */
    public Class<?> findClass(String name) {
        try {
            URLClassLoader child  = new URLClassLoader(new URL[] {new URL("file:" + this.file.toString())}, PoloCloudAPI.class.getClassLoader());
            return Class.forName(name, true, child);
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds a class via jar entry
     *
     * @param name the name of the class
     * @return class by name but uses JarEntry
     */
    public Class<?> findClassWithJarEntry(String name) {
        try {
            JarFile jarFile = new JarFile(file);
            Enumeration<JarEntry> e = jarFile.entries();

            URL[] urls = {new URL("jar:file:" + file.getAbsolutePath())};
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                if(je.isDirectory() || !je.getName().endsWith(".class")){
                    continue;
                }
                if (je.getName().contains(name)){
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                    return cl.loadClass(className);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return null;
    }
}
