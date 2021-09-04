package de.polocloud.api.network.protocol.packet.base.response.base.elements;

import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;

public class StringElement extends DefaultElement {

    public StringElement(IResponse response, String key) {
        super(response, key);
    }

    @Override
    public String getAsString() {
        return response.getDocument().getString(key);
    }
}
