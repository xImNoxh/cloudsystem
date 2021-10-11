package de.polocloud.npcs.npc.entity.nms.wrapper;

import org.bukkit.Bukkit;

public enum NMSClassWrapper {

    CRAFT_ENTITY_CLASS("org.bukkit.craftbukkit.%version%.entity.CraftEntity"),
    BUKKIT_ENTITY_CLASS("net.minecraft.server.%version%.Entity"),
    NBT_TAG_COMPOUND_CLASS("net.minecraft.server.%version%.NBTTagCompound");

    private String className;
    private Class<?> clazz;

    NMSClassWrapper(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static void loadClasses(){
        String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        for (NMSClassWrapper value : values()) {
            value.className = value.getClassName().replace("%version%", bukkitVersion);
            try{
                value.clazz = Class.forName(value.getClassName());
            } catch (ClassNotFoundException exception){
                System.err.println("Error while initializing classes. Failed to initialize: " + value.getClassName());
            }
        }
    }

    public static String getVersion(){
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

}
