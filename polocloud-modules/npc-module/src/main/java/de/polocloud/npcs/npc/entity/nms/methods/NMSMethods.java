package de.polocloud.npcs.npc.entity.nms.methods;

import de.polocloud.npcs.npc.entity.nms.reflection.ReflectionProvider;
import de.polocloud.npcs.npc.entity.nms.wrapper.NMSClassWrapper;

public enum NMSMethods {

    ENTITY_INIT_NBT(NMSClassWrapper.BUKKIT_ENTITY_CLASS,"c", new Class[]{NMSClassWrapper.NBT_TAG_COMPOUND_CLASS.getClazz()}),
    ENTITY_SET_NBT(NMSClassWrapper.BUKKIT_ENTITY_CLASS,"f", new Class[]{NMSClassWrapper.NBT_TAG_COMPOUND_CLASS.getClazz()}),
    ENTITY_GET_BUKKIT_ENTITY(NMSClassWrapper.BUKKIT_ENTITY_CLASS, "getBukkitEntity", null),
    CRAFT_ENTITY_GET_HANDLE(NMSClassWrapper.CRAFT_ENTITY_CLASS, "getHandle", null),
    NBT_TAG_COMPOUND_TO_STRING(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"toString", null),
    NBT_TAG_COMPOUND_GET(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"get", new Class[]{String.class}),
    NBT_TAG_COMPOUND_SET_BOOLEAN(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setBoolean", new Class[]{String.class, boolean.class}),
    NBT_TAG_COMPOUND_SET_BYTE(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setByte", new Class[]{String.class, byte.class}),
    NBT_TAG_COMPOUND_SET_DOUBLE(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setDouble", new Class[]{String.class, double.class}),
    NBT_TAG_COMPOUND_SET_FLOAT(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setFloat", new Class[]{String.class, float.class}),
    NBT_TAG_COMPOUND_SET_INT(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setInt", new Class[]{String.class, int.class}),
    NBT_TAG_COMPOUND_SET_LONG(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setLong", new Class[]{String.class, long.class}),
    NBT_TAG_COMPOUND_SET_SHORT(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setShort", new Class[]{String.class, short.class}),
    NBT_TAG_COMPOUND_SET_STRING(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS,"setString", new Class[]{String.class, String.class});

    private final NMSClassWrapper classWrapper;
    private final String methodName;
    private final Class<?>[] methodArgs;

    NMSMethods(NMSClassWrapper classWrapper, String methodName, Class<?>[] methodArgs) {
        this.classWrapper = classWrapper;
        this.methodName = methodName;
        this.methodArgs = methodArgs;
    }

    public Object invoke(Object instance, Object... args) {
        return ReflectionProvider.invokeClassMethod(classWrapper.getClazz(), instance, methodName, methodArgs, args);
    }

}
