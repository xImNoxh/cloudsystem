package de.polocloud.proxy.config.slots;

public class SlotSync {

    private boolean syncSlots = true;
    private boolean dynamicSlots = false;

    private int allMaxPlayers = 10;

    private SlotCommandConfig slotCommandConfig;

    public SlotSync() {
        this.slotCommandConfig = new SlotCommandConfig();
    }

    public SlotCommandConfig getSlotCommandConfig() {
        return slotCommandConfig;
    }

    public void setSlotCommandConfig(SlotCommandConfig slotCommandConfig) {
        this.slotCommandConfig = slotCommandConfig;
    }

    public boolean isSyncSlots() {
        return syncSlots;
    }

    public void setSyncSlots(boolean syncSlots) {
        this.syncSlots = syncSlots;
    }

    public boolean isDynamicSlots() {
        return dynamicSlots;
    }

    public void setDynamicSlots(boolean dynamicSlots) {
        this.dynamicSlots = dynamicSlots;
    }

    public int getAllMaxPlayers() {
        return allMaxPlayers;
    }

    public void setAllMaxPlayers(int allMaxPlayers) {
        this.allMaxPlayers = allMaxPlayers;
    }
}
