package de.polocloud.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.common.ExceptionSupplier;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PoloUtils {

    /**
     * The {@link Gson} instance to only instantiate once and do not waste resources
     */
    public static final Gson GSON_INSTANCE = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create();

    /**
     * Returns an object from an {@link ExceptionSupplier}
     * and throws exception if any occurs
     *
     * @param tSupplier the supplier returning the object
     * @param <T> the generic-type
     * @return the object or null if exception occured
     */
    public static <T> T sneakyThrows(ExceptionSupplier<T> tSupplier) {

        try {
            return tSupplier.supply();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            Method enumConstantDirectory = enumType.getClass().getDeclaredMethod("enumConstantDirectory");
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
            return null;
        }
    }
}
