package de.polocloud.signs.protocol;

import de.polocloud.signs.module.config.messages.SignMessagesConfig;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.layout.Layout;
import de.polocloud.signs.sign.location.SignLocation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for transferring all Config values from
 * PoloCloud-Module config to the Spigot/Bukkit servers
 */
public class GlobalConfigClass {

    private HashMap<SignState, Layout[]> signLayouts = new HashMap<>();

    private SignMessagesConfig signMessages = new SignMessagesConfig();

    private ArrayList<SignLocation> locations = new ArrayList<>();

    public HashMap<SignState, Layout[]> getSignLayouts() {
        return signLayouts;
    }

    public void setSignLayouts(HashMap<SignState, Layout[]> signLayouts) {
        this.signLayouts = signLayouts;
    }

    public SignMessagesConfig getSignMessages() {
        return signMessages;
    }

    public void setSignMessages(SignMessagesConfig signMessages) {
        this.signMessages = signMessages;
    }

    public ArrayList<SignLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<SignLocation> locations) {
        this.locations = locations;
    }

}
