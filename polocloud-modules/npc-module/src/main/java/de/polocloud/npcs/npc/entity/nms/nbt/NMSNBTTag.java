package de.polocloud.npcs.npc.entity.nms.nbt;

import de.polocloud.npcs.npc.entity.nms.methods.NMSMethods;
import de.polocloud.npcs.npc.entity.nms.reflection.ReflectionProvider;
import de.polocloud.npcs.npc.entity.nms.wrapper.NMSClassWrapper;

public class NMSNBTTag {

    private final Object nms;

    public NMSNBTTag() {
        nms = ReflectionProvider.getNewInstanceOfClass(NMSClassWrapper.NBT_TAG_COMPOUND_CLASS.getClazz(), null);
    }

    public NMSNBTTag(Object nmsNbtTagCompound) {
        nms = nmsNbtTagCompound;
    }

    public Object asNMS() {
        return nms;
    }

    public String asString() {
        return (String) NMSMethods.NBT_TAG_COMPOUND_TO_STRING.invoke(nms);
    }

    public void setInt(String section, int value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_INT.invoke(nms, section, value);
    }

    public void setShort(String section, short value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_SHORT.invoke(nms, section, value);
    }

    public void setByte(String section, byte value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_BYTE.invoke(nms, section, value);
    }


    public void setLong(String section, long value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_LONG.invoke(nms, section, value);
    }

    public void setBoolean(String section, boolean value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_BOOLEAN.invoke(nms, section, value);
    }

    public void setString(String section, String value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_STRING.invoke(nms, section, value);
    }

    public void setDouble(String section, double value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_DOUBLE.invoke(nms, section, value);
    }

    public void setFloat(String section, float value) {
        NMSMethods.NBT_TAG_COMPOUND_SET_FLOAT.invoke(nms, section, value);
    }

    public NMSNBTTag get(String section) {
        Object nmsNbtTagCompound = NMSMethods.NBT_TAG_COMPOUND_GET.invoke(nms, section);
        return new NMSNBTTag(nmsNbtTagCompound);
    }

}
