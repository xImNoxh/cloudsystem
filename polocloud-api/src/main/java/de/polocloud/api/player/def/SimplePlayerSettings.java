package de.polocloud.api.player.def;

import de.polocloud.api.player.extras.IPlayerSettings;
import de.polocloud.api.util.PoloHelper;

import java.util.Locale;

public class SimplePlayerSettings implements IPlayerSettings {

    private final Locale locale;
    private final boolean chatColors;
    private final byte renderDistance;
    private final boolean hat, jacket, rightSleeve, leftSleeve, rightPants, leftPants, cape;

    private final IPlayerSettings.ChatMode chatMode;
    private final IPlayerSettings.MainHand mainHand;

    public SimplePlayerSettings(Locale locale, boolean chatColors, byte renderDistance, boolean hat, boolean jacket, boolean rightSleeve, boolean leftSleeve, boolean rightPants, boolean leftPants, boolean cape, ChatMode chatMode, MainHand mainHand) {
        this.locale = locale;
        this.chatColors = chatColors;
        this.renderDistance = renderDistance;
        this.hat = hat;
        this.jacket = jacket;
        this.rightSleeve = rightSleeve;
        this.leftSleeve = leftSleeve;
        this.rightPants = rightPants;
        this.leftPants = leftPants;
        this.cape = cape;
        this.chatMode = chatMode;
        this.mainHand = mainHand;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public byte getRenderDistance() {
        return renderDistance;
    }

    @Override
    public boolean hasCape() {
        return cape;
    }

    @Override
    public boolean hasJacket() {
        return jacket;
    }

    @Override
    public boolean hasLeftSleeve() {
        return leftSleeve;
    }

    @Override
    public boolean hasRightSleeve() {
        return rightSleeve;
    }

    @Override
    public boolean hasLeftPants() {
        return leftPants;
    }

    @Override
    public boolean hasRightPants() {
        return rightPants;
    }

    @Override
    public boolean hasHat() {
        return hat;
    }

    @Override
    public boolean hasChatColors() {
        return chatColors;
    }

    @Override
    public MainHand getMainHand() {
        return mainHand;
    }

    @Override
    public ChatMode getChatMode() {
        return chatMode;
    }

    @Override
    public String toString() {
        return PoloHelper.GSON_INSTANCE.toJson(this);
    }

}
