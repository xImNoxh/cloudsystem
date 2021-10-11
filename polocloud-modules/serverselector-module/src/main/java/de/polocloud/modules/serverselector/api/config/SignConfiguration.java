package de.polocloud.modules.serverselector.api.config;

import de.polocloud.modules.serverselector.api.elements.SignAnimation;
import de.polocloud.modules.serverselector.api.elements.SignLayout;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class SignConfiguration {

    /**
     * The knockback config
     */
    private final KnockbackConfig knockBackConfig;

    /**
     * The loading animation
     */
    private final SignAnimation loadingLayout;

    /**
     * The online sign layout
     */
    private final SignLayout onlineLayout;

    /**
     * The full sign layout
     */
    private final SignLayout fullLayout;

    /**
     * The maintenance sign layout
     */
    private final SignLayout maintenanceLayout;

    /**
     * The starting sign layout
     */
    private final SignLayout startingLayOut;

    /**
     * Creates the default {@link SignConfiguration}
     * with the default {@link KnockbackConfig}, {@link SignAnimation}
     * and other {@link SignLayout}s
     */
    public SignConfiguration() {
        this(
            new KnockbackConfig(
                true,
                0.7,
                0.5,
                "cloudsystem.signs.bypass"
            ),
            new SignAnimation(
                20,
                new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛&7⬛⬛", ""}, "STAINED_CLAY", 14),
                new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛⬛&7⬛", ""}, "STAINED_CLAY", 14),
                new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &a⬛⬛⬛", ""}, "STAINED_CLAY", 14),
                new SignLayout("LOADING", new String[]{"", "&8│ &bLoading... &8│", "&7%group% &8x &7⬛⬛⬛", ""}, "STAINED_CLAY", 14)
            ),
            new SignLayout("ONLINE", new String[]{"&8│ &b%server% &8│", "&aAvailable", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 5),
            new SignLayout("FULL", new String[]{"&8│ &b%server% &8│", "&6VIP", "%motd%", "&8× &7%online%&8/&7%max% &8×"}, "STAINED_CLAY", 1),
            new SignLayout("MAINTENANCE", new String[]{"", "&8│ &b%group% &8│", "&8× &cMaintenance &8×", ""}, "STAINED_CLAY", 3),
            new SignLayout("STARTING", new String[]{"", "&8│ &e%server% &8│", "&8× &0Starting... &8×", ""}, "STAINED_CLAY", 4)
        );
    }

}
