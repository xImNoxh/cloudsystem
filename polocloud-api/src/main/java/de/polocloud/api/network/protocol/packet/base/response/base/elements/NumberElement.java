package de.polocloud.api.network.protocol.packet.base.response.base.elements;

import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;

public class NumberElement extends DefaultElement {

    private final Number number;
    
    public NumberElement(IResponse response, String key, Number number) {
        super(response, key);
        
        this.number = number;
    }

    public double getAsDouble() {
        return this.number.doubleValue();
    }
    
    public float getAsFloat() {
        return this.number.floatValue();
    }

    public long getAsLong() {
        return this.number.longValue();
    }

    public short getAsShort() {
        return this.number.shortValue();
    }

    public int getAsInt() {
        return this.number.intValue();
    }

    public byte getAsByte() {
        return this.number.byteValue();
    }
}
