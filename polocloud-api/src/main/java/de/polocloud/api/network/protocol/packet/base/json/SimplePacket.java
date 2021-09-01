package de.polocloud.api.network.protocol.packet.base.json;

import com.google.gson.JsonElement;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.packet.base.Packet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class SimplePacket extends Packet {

    @Override
    public final void read(IPacketBuffer buf) throws IOException {

        try {
            String json = buf.readString();
            JsonData jsonObject = new JsonData(json);

            for (Class<?> aClass : loadAllSubClasses(this.getClass())) {
                for (String name : jsonObject.keySet()) {
                    JsonData sub = jsonObject.getData(name);

                    String cl = sub.getString("typeClass");

                    if (cl.equalsIgnoreCase("int")) {
                        cl = "java.lang.Integer";
                    } else if (cl.equalsIgnoreCase("boolean")) {
                        cl = "java.lang.Boolean";
                    } else if (cl.equalsIgnoreCase("double")) {
                        cl = "java.lang.Double";
                    } else if (cl.equalsIgnoreCase("short")) {
                        cl = "java.lang.Short";
                    } else if (cl.equalsIgnoreCase("float")) {
                        cl = "java.lang.Float";
                    } else if (cl.equalsIgnoreCase("long")) {
                        cl = "java.lang.Long";
                    } else if (cl.equalsIgnoreCase("byte")) {
                        cl = "java.lang.Byte";
                    }

                     try {
                        Class<?> typeClass = Class.forName(cl);
                         Object value1;

                         if (sub.has("generic") && typeClass.equals(List.class)) {
                             value1 = new LinkedList<>();
                             if (sub.has("wrapperClass")) {
                                 Class<?> wrapperClass = Class.forName(sub.getString("wrapperClass"));

                                 for (JsonElement value : sub.getElement("value").getAsJsonArray()) {
                                     ((List)value1).add(JsonData.GSON.fromJson(value, wrapperClass));
                                 }
                             }
                         } else {
                             value1 = JsonData.GSON.fromJson(sub.getElement("value").toString(), typeClass);
                         }

                         try {
                             Field declaredField = aClass.getDeclaredField(name);
                             declaredField.setAccessible(true);
                             declaredField.set(this, value1);
                         } catch (NoSuchFieldException ignored) {  }
                    } catch (ClassNotFoundException e) {
                         //Ignoring
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public final void write(IPacketBuffer buf) throws IOException {
        try {
            JsonData jsonObject = new JsonData();
            for (Class<?> subClass : loadAllSubClasses(this.getClass())) {
                for (Field declaredField : subClass.getDeclaredFields()) {
                    declaredField.setAccessible(true);

                    PacketSerializable annotation = declaredField.getAnnotation(PacketSerializable.class);
                    if (annotation != null) {
                        Object o = declaredField.get(this);
                        JsonData sub = new JsonData();
                        sub.append("key", declaredField.getName());
                        sub.append("value", o);

                        if (o instanceof List && annotation.value() != Class.class) {
                            sub.append("typeClass", List.class.getName());
                            sub.append("generic", annotation.value().getName());
                            if (!((List<?>) o).isEmpty()) {
                                sub.append("wrapperClass", ((List<?>) o).get(0).getClass().getName());
                            }
                        } else {
                            sub.append("typeClass", annotation.value() == Class.class ? o.getClass().getName() : annotation.value().getName());
                        }

                        jsonObject.append(declaredField.getName(), sub);
                    }
                }
            }

            buf.writeString(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all subclasses (extended classes)
     * of another class
     *
     * @param clazz the start-class
     * @return set of classes
     */
    private List<Class<?>> loadAllSubClasses(Class<?> clazz) {
        List<Class<?>> res = new ArrayList<>();

        do {
            res.add(clazz);

            // First, add all the interfaces implemented by this class
            Class<?>[] interfaces = clazz.getInterfaces();
            if (interfaces.length > 0) {
                res.addAll(Arrays.asList(interfaces));

                for (Class<?> interfaze : interfaces) {
                    res.addAll(loadAllSubClasses(interfaze));
                }
            }

            // Add the super class
            Class<?> superClass = clazz.getSuperclass();

            // Interfaces does not have java,lang.Object as superclass, they have null, so break the cycle and return
            if (superClass == null) {
                break;
            }

            // Now inspect the superclass
            clazz = superClass;
        } while (!"java.lang.Object".equals(clazz.getCanonicalName()));

        res.remove(IProtocolObject.class);
        res.remove(Packet.class);
        res.remove(SimplePacket.class);
        return res;
    }

}
