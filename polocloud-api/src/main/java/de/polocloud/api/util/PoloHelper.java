package de.polocloud.api.util;

import com.google.gson.*;
import de.polocloud.api.common.ExceptionRunnable;
import de.polocloud.api.common.ExceptionSupplier;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.api.module.info.ModuleInfo;
import io.netty.channel.ChannelFutureListener;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class contains mostly static
 * fields and methods to easier access util methods and constants
 * like constant name fields or something
 */
public class PoloHelper {

    /**
     * The {@link Gson} instance to only instantiate once and do not waste resources
     */
    public static final Gson GSON_INSTANCE = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    /**
     * The date format to format dates with Day-Month-Year and hour:minute:second (28.08.2021 - 23:08:53)
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");

    /**
     * The console prefix
     */
    public static final String CONSOLE_PREFIX = MinecraftColor.LIGHT_BLUE + "PoloCloud " + MinecraftColor.GRAY + "» ";


    /**
     * The polo type field
     */
    public static PoloType PRE_POLO_TYPE = null;

    public static String getSimpleTime() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public static String getSimpleDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public static int generatePort() {
        int port = 0;
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }
    /**
     * Gets a NMS Class for a name
     *
     * @param name the name
     * @return class or null
     */
    public static Class<?> getNMSClass(String name, String version) {
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Sends a packet to Bukkit player
     *
     * @param to the receiver
     * @param packet the packet
     */
    public static void sendPacket(Object to, String version, Object packet) {
        try {
            Object playerHandle = to.getClass().getMethod("getHandle", new Class[0]).invoke(to);
            Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
            playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet", version) }).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Deletes a folder with content
     *
     * @param folder the folder
     */
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();

        //some JVMs return null for empty dirs
        if(files != null) {
            for (File f: files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }
    public static ChannelFutureListener getChannelFutureListener(Class<?> instance) {
        return channelFuture -> {
            if (!channelFuture.isSuccess()) {
                System.out.println("[" + instance.getName() + "]" + " ran into an error:");
                channelFuture.cause().printStackTrace();
            }
        };
    }

    public static void println(Class<?> cls, String line) {
        System.out.println("[" + cls.getSimpleName() + "] " + line);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    public static Class<?>[] getClasses(String packageName, Class<?> instanceClass) throws ClassNotFoundException, IOException {

        ClassLoader classLoader = instanceClass.getClassLoader();
        if (classLoader == null) {
            return new Class[0];
        }

        Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
        List<Class<?>> classes = new LinkedList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            classes.addAll(findClasses(new File(resource.getFile()), packageName));
        }
        return classes.toArray(new Class[0]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    /**
     * Returns an object from an {@link ExceptionSupplier}
     * and throws exception if any occurs
     *
     * @param tSupplier the supplier returning the object
     * @param <T> the generic-type
     * @return the object or null if exception occurred
     */
    public static <T> T sneakyThrows(ExceptionSupplier<T> tSupplier) {
        try {
            return tSupplier.supply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Something went wrong while trying to sneaky Throw !");
    }

    /**
     * Sneaky throws exception for a given runnable
     *
     * @param runnable the runnable
     */
    public static void sneakyThrows(ExceptionRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Helper method for getMethodSignature
    private static String getClassName(Class<?> cls) {
        return (cls.isArray()) ? getClassName(cls.getComponentType()) + "[]" : cls.getSimpleName();
    }

    /**
     * Gets the signature of a {@link Method}
     * @param method the method
     */
    public static String getMethodSignature(Method method) {
        StringBuilder methodSignature = new StringBuilder(method.getDeclaringClass().getName() + "#" + method.getName() + "(");
        boolean first = true;
        for (Class<?> param : method.getParameterTypes()) {
            if (!first)
                methodSignature.append(", ");
            methodSignature.append(getClassName(param));
            first = false;
        }
        methodSignature.append(")");
        return methodSignature.toString();
    }

    /**
     * Gets an {@link Enum} by its name
     *
     * @param enumType the class of the enum
     * @param name the name of the enum value
     * @return enum or null
     */
    public static Enum<?> getEnumByName(Class<?> enumType, String name) {
        try {
            Method enumConstantDirectory = enumType.getDeclaredMethod("enumConstantDirectory");
            enumConstantDirectory.setAccessible(true);

            Map<String, Enum<?>> invoke = (Map<String, Enum<?>>) enumConstantDirectory.invoke(enumType);

            Enum<?> result = invoke.get(name);
            if (result != null) {
                return result;
            }
            if (name == null) {
                throw new NullPointerException("Name is null");
            }
            throw new IllegalArgumentException("No enum constant " + enumType.getCanonicalName() + "." + name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @author kaimu-kun
     * @see <a href="https://github.com/kaimu-kun/hastebin.java">...</a>
     */
    public static String uploadToPasteBin(String text, boolean raw) throws IOException {
        byte[] postData = text.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String requestURL = "https://hastebin.com/documents";
        URL url = new URL(requestURL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", "Hastebin Java Api");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        String response = null;
        DataOutputStream wr;
        try {
            wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (response.contains("\"key\"")) {
            response = response.substring(response.indexOf(":") + 2, response.length() - 2);

            String postURL = raw ? "https://hastebin.com/raw/" : "https://hastebin.com/";
            response = postURL + response;
        }

        return response;
    }

    /**
     * Creates an Object from scratch
     *
     * @param tClass the object class
     */
    public static <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<?> constructor;

            try {
                List<Constructor<?>> constructors = Arrays.asList(tClass.getDeclaredConstructors());

                constructors.sort(Comparator.comparingInt(Constructor::getParameterCount));

                constructor = constructors.get(constructors.size() - 1);
            } catch (Exception e) {
                constructor = null;
            }

            //Iterates through all Constructors to create a new Instance of the Object
            //And to set all values to null, -1 or false
            T object = null;
            if (constructor != null) {
                Object[] args = new Object[constructor.getParameters().length];
                for (int i = 0; i < constructor.getParameterTypes().length; i++) {
                    final Class<?> parameterType = constructor.getParameterTypes()[i];
                    if (Number.class.isAssignableFrom(parameterType)) {
                        args[i] = -1;
                    } else if (parameterType.equals(boolean.class) || parameterType.equals(Boolean.class)) {
                        args[i] = false;
                    } else if (parameterType.equals(int.class) || parameterType.equals(double.class) || parameterType.equals(short.class) || parameterType.equals(long.class) || parameterType.equals(float.class) || parameterType.equals(byte.class)) {
                        args[i] = -1;
                    } else if (parameterType.equals(Integer.class) || parameterType.equals(Double.class) || parameterType.equals(Short.class) || parameterType.equals(Long.class) || parameterType.equals(Float.class) || parameterType.equals(Byte.class)) {
                        args[i] = -1;
                    } else {
                        args[i] = null;
                    }
                }
                object = (T) constructor.newInstance(args);
            }

            if (object == null) {
                object = tClass.newInstance();
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void checkObjectForNulls(Object obj, Consumer<Field> nulledField) {
        sneakyThrows(() -> {
            for (Field declaredField : obj.getClass().getDeclaredFields()) {
                declaredField.setAccessible(true);
                if (declaredField.get(obj) == null) {
                    nulledField.accept(declaredField);
                }
            }
        });
    }
}
