package de.polocloud.api.network.protocol.packet.base.response.base.elements;

import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponseElement;

public class DefaultElement implements IResponseElement {

    protected final IResponse response;
    protected final String key;

    public DefaultElement(IResponse response, String key) {
        this.response = response;
        this.key = key;
    }

    @Override
    public boolean isNull() {
        return !response.getDocument().has(this.key);
    }

    @Override
    public double getAsDouble() {
        throw new UnsupportedOperationException("Element is not a Double!");
    }

    @Override
    public float getAsFloat() {
        throw new UnsupportedOperationException("Element is not a Float!");
    }

    @Override
    public long getAsLong() {
        throw new UnsupportedOperationException("Element is not a Long!");
    }

    @Override
    public byte getAsByte() {
        throw new UnsupportedOperationException("Element is not a Byte!");
    }

    @Override
    public short getAsShort() {
        throw new UnsupportedOperationException("Element is not a Short!");
    }
    @Override
    public int getAsInt() {
        throw new UnsupportedOperationException("Element is not an Integer!");
    }

    @Override
    public String getAsString() {
        throw new UnsupportedOperationException("Element is not a String!");
    }

    @Override
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException("Element is not a Boolean!");
    }

    @Override
    public <T> T getAsCustom(Class<T> typeClass) {
        JsonData document = this.response.getDocument();
        return document.getObject(this.key, typeClass);
    }
}
