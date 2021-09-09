package de.polocloud.npcs.npc.base.meta;

public class CloudNPCMeta {

    private int x, y, z;
    private String world, templateOrGameServer, skinName, itemInHand, entityName;
    private boolean onlyGameServer, entity;

    public CloudNPCMeta(int x, int y, int z, String world, String templateOrGameServer, String skinName, String itemInHand, String entityName, boolean onlyGameServer, boolean entity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.templateOrGameServer = templateOrGameServer;
        this.skinName = skinName;
        this.itemInHand = itemInHand;
        this.entityName = entityName;
        this.onlyGameServer = onlyGameServer;
        this.entity = entity;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getTemplateOrGameServer() {
        return templateOrGameServer;
    }

    public void setTemplateOrGameServer(String templateOrGameServer) {
        this.templateOrGameServer = templateOrGameServer;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public String getItemInHand() {
        return itemInHand;
    }

    public void setItemInHand(String itemInHand) {
        this.itemInHand = itemInHand;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean isOnlyGameServer() {
        return onlyGameServer;
    }

    public void setOnlyGameServer(boolean onlyGameServer) {
        this.onlyGameServer = onlyGameServer;
    }

    public boolean isEntity() {
        return entity;
    }

    public void setEntity(boolean entity) {
        this.entity = entity;
    }
}
