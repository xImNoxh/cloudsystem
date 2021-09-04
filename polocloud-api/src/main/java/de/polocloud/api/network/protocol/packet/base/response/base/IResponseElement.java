package de.polocloud.api.network.protocol.packet.base.response.base;

public interface IResponseElement {

    double getAsDouble();

    float getAsFloat();

    long getAsLong();

    byte getAsByte();

    short getAsShort();

    int getAsInt();

    boolean isNull();

    String getAsString();

    boolean getAsBoolean();

    <T> T getAsCustom(Class<T> typeClass);
}
