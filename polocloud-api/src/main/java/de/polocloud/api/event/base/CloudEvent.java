package de.polocloud.api.event.base;


public abstract class CloudEvent {

    /**
     * If this packet was just sent via netty
     */
    private boolean nettyFired;


    public boolean isNettyFired() {
        return nettyFired;
    }

    public void setNettyFired(boolean nettyFired) {
        this.nettyFired = nettyFired;
    }
}
