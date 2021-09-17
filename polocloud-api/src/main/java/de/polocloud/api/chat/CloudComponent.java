package de.polocloud.api.chat;


import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import javafx.util.Pair;
import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public class CloudComponent implements IProtocolObject {

    /**
     * The message of this component
     */
    private String message;

    /**
     * All cached hover events
     */
    private List<Pair<HoverAction, String>> hoverEvents;

    /**
     * All cached click events
     */
    private List<Pair<ClickAction, String>> clickEvents;

    /**
     * The list of other components
     */
    private List<CloudComponent> subComponents;

    /**
     * Constructs a component with a message
     *
     * @param message the message
     */
    public CloudComponent(String message) {
        this.message = message;
        this.clickEvents = new ArrayList<>();
        this.hoverEvents = new ArrayList<>();
        this.subComponents = new ArrayList<>();
    }

    /**
     * Adds a hover event to this component
     *
     * @param hoverAction the action
     * @param value the value
     * @return current component
     */
    public CloudComponent addHover(HoverAction hoverAction, String value) {
        this.hoverEvents.add(new Pair<>(hoverAction, value));
        return this;
    }

    /**
     * Adds a click event to this component
     *
     * @param clickAction the action
     * @param value the value
     * @return current component
     */
    public CloudComponent addClick(ClickAction clickAction, String value) {
        this.clickEvents.add(new Pair<>(clickAction, value));
        return this;
    }

    /**
     * Adds another component to chain
     *
     * @param cloudComponent the component to add
     * @return this component
     */
    public CloudComponent append(CloudComponent cloudComponent) {
        this.subComponents.add(cloudComponent);
        return this;
    }

    @Override
    public String toString() {
        return this.message;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeString(this.message);

        //Hover events
        buf.writeInt(hoverEvents.size());
        for (Pair<HoverAction, String> hoverEvent : hoverEvents) {
            buf.writeEnum(hoverEvent.getKey());
            buf.writeString(hoverEvent.getValue());
        }

        //Click events
        buf.writeInt(clickEvents.size());
        for (Pair<ClickAction, String> clickEvent : clickEvents) {
            buf.writeEnum(clickEvent.getKey());
            buf.writeString(clickEvent.getValue());
        }

        //Other components
        buf.writeInt(subComponents.size());
        for (CloudComponent cloudComponent : subComponents) {
            buf.writeProtocol(cloudComponent);
        }
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.message = buf.readString();

        //Hover events
        int hoverSize = buf.readInt();
        this.hoverEvents = new ArrayList<>(hoverSize);
        for (int i = 0; i < hoverSize; i++) {
            this.hoverEvents.add(new Pair<>(buf.readEnum(), buf.readString()));
        }

        //Click events
        int clickSize = buf.readInt();
        this.clickEvents = new ArrayList<>(clickSize);
        for (int i = 0; i < clickSize; i++) {
            this.clickEvents.add(new Pair<>(buf.readEnum(), buf.readString()));
        }

        //Other components
        int componentSize = buf.readInt();
        this.subComponents = new ArrayList<>(componentSize);
        for (int i = 0; i < componentSize; i++) {
            this.subComponents.add(buf.readProtocol());
        }
    }
}
