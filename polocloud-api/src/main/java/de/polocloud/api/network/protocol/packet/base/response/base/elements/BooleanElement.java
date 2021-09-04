package de.polocloud.api.network.protocol.packet.base.response.base.elements;

import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;

public class BooleanElement extends DefaultElement {


    public BooleanElement(IResponse response, String key) {
        super(response, key);

    }

    @Override
    public boolean getAsBoolean() {
        return this.response.getDocument().getBoolean(key);
    }
}
