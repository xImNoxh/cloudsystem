package de.polocloud.api.event.handling;

public enum EventPriority {

    HIGH(-1), //Will be called first
    NORMAL(0), // Will be called standard
    LOW(1); //Will be called last

    public final int value;

    EventPriority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
