package de.polocloud.api.gameserver.helper;

import de.polocloud.api.template.helper.TemplateType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor @Getter
public enum GameServerVersion implements Serializable {

    //Other bungeecords
    BUNGEE("https://ci.md-5.net/job/BungeeCord/lastBuild/artifact/bootstrap/target/BungeeCord.jar", "BungeeCord", TemplateType.PROXY, -1, false),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.17/builds/448/downloads/waterfall-1.17-448.jar", "Waterfall", TemplateType.PROXY, -1, false),

    //Velocity
    VELOCITY_3_0_1("https://versions.velocitypowered.com/download/3.0.1.jar", "Velocity-3.0.1", TemplateType.PROXY, -1, false),

    //Paper-Spigot
    PAPERSPIGOT_1_17_1("https://papermc.io/api/v2/projects/paper/versions/1.17.1/builds/311/downloads/paper-1.17.1-311.jar", "Paper-1.17.1", TemplateType.MINECRAFT, 756, true),
    PAPERSPIGOT_1_16_5("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/788/downloads/paper-1.16.5-788.jar", "Paper-1.16.5", TemplateType.MINECRAFT, 754, true),
    PAPERSPIGOT_1_15_2("https://papermc.io/api/v2/projects/paper/versions/1.15.2/builds/391/downloads/paper-1.15.2-391.jar", "Paper-1.15.2", TemplateType.MINECRAFT, 578, true),
    PAPERSPIGOT_1_14_4("https://papermc.io/api/v2/projects/paper/versions/1.14.4/builds/243/downloads/paper-1.14.4-243.jar", "Paper-1.14.4", TemplateType.MINECRAFT, 498, true),
    PAPERSPIGOT_1_13_2("https://papermc.io/api/v2/projects/paper/versions/1.13.2/builds/655/downloads/paper-1.13.2-655.jar", "Paper-1.13.2", TemplateType.MINECRAFT, 404, true),
    PAPERSPIGOT_1_12_2("https://papermc.io/api/v2/projects/paper/versions/1.12.2/builds/1618/downloads/paper-1.12.2-1618.jar", "Paper-1.12.2", TemplateType.MINECRAFT, 340, true),
    PAPERSPIGOT_1_9_4("https://papermc.io/api/v2/projects/paper/versions/1.9.4/builds/773/downloads/paper-1.9.4-773.jar", "Paper-1.9.4", TemplateType.MINECRAFT, 110, true),
    PAPERSPIGOT_1_8_8("https://papermc.io/api/v2/projects/paper/versions/1.8.8/builds/443/downloads/paper-1.8.8-443.jar", "Paper-1.8.8", TemplateType.MINECRAFT, 47, true),

    //Normal Spigot
    SPIGOT_1_16_5("https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar", "Spigot-1.16.5", TemplateType.MINECRAFT, 754, false),
    SPIGOT_1_16_4("https://cdn.getbukkit.org/spigot/spigot-1.16.4.jar", "Spigot-1.16.4", TemplateType.MINECRAFT, 754, false),
    SPIGOT_1_16_3("https://cdn.getbukkit.org/spigot/spigot-1.16.3.jar", "Spigot-1.16.3", TemplateType.MINECRAFT, 753, false),
    SPIGOT_1_16_2("https://cdn.getbukkit.org/spigot/spigot-1.16.2.jar", "Spigot-1.16.2", TemplateType.MINECRAFT, 751, false),
    SPIGOT_1_16_1("https://cdn.getbukkit.org/spigot/spigot-1.16.1.jar", "Spigot-1.16.1", TemplateType.MINECRAFT, 736, false),
    SPIGOT_1_16("https://cdn.getbukkit.org/spigot/spigot-1.16.jar", "Spigot-1.16", TemplateType.MINECRAFT, 735, false),
    SPIGOT_1_15_2("https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar", "Spigot-1.15.2", TemplateType.MINECRAFT, 578, false),
    SPIGOT_1_15_1("https://cdn.getbukkit.org/spigot/spigot-1.15.1.jar", "Spigot-1.15.1", TemplateType.MINECRAFT, 575, false),
    SPIGOT_1_15("https://cdn.getbukkit.org/spigot/spigot-1.15.jar", "Spigot-1.15", TemplateType.MINECRAFT, 573, false),
    SPIGOT_1_14_4("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar", "Spigot-1.14.4", TemplateType.MINECRAFT, 498, false),
    SPIGOT_1_14_3("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar", "Spigot-1.14.3", TemplateType.MINECRAFT, 490, false),
    SPIGOT_1_14_2("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar", "Spigot-1.14.2", TemplateType.MINECRAFT, 485, false),
    SPIGOT_1_14("https://cdn.getbukkit.org/spigot/spigot-1.14.jar", "Spigot-1.14", TemplateType.MINECRAFT, 477, false),
    SPIGOT_1_13_2("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar", "Spigot-1.13.2", TemplateType.MINECRAFT, 404, false),
    SPIGOT_1_13_1("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar", "Spigot-1.13.1", TemplateType.MINECRAFT, 401, false),
    SPIGOT_1_13("https://cdn.getbukkit.org/spigot/spigot-1.13.jar", "Spigot-1.13", TemplateType.MINECRAFT, 393, false),
    SPIGOT_1_12_2("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar", "Spigot-1.12.2", TemplateType.MINECRAFT, 340, false),
    SPIGOT_1_12_1("https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar", "Spigot-1.12.1", TemplateType.MINECRAFT, 338, false),
    SPIGOT_1_12("https://cdn.getbukkit.org/spigot/spigot-1.12.jar", "Spigot-1.12", TemplateType.MINECRAFT, 335, false),
    SPIGOT_1_11_1("https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar", "Spigot-1.11.1", TemplateType.MINECRAFT, 316, false),
    SPIGOT_1_11("https://cdn.getbukkit.org/spigot/spigot-1.11.jar", "Spigot-1.11", TemplateType.MINECRAFT, 315, false),
    SPIGOT_1_10("https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar", "Spigot-1.10", TemplateType.MINECRAFT, 210, false),
    SPIGOT_1_9_4("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.4", TemplateType.MINECRAFT, 110, false),
    SPIGOT_1_9_2("https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.2", TemplateType.MINECRAFT, 109, false),
    SPIGOT_1_9("https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9", TemplateType.MINECRAFT, 107, false),
    SPIGOT_1_8_8("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", "Spigot-1.8.8", TemplateType.MINECRAFT, 47, false),
    ;

    /*

     MINECRAFT_1_17(755, "1.17");
     *
     */

    private String url;
    private String title;
    private TemplateType templateType;
    private int protocolId;
    private boolean isPatchable;


    public static GameServerVersion getVersion(String title) {
        for (GameServerVersion value : values()) {
            if (value.getTitle().equalsIgnoreCase(title)) {
                return value;
            }
        }
        return null;
    }

    public static Object[] prettyValues(TemplateType templateType) {
        return Arrays.stream(values()).filter(key -> key.getTemplateType().equals(templateType)).map(GameServerVersion::getTitle).toArray();
    }

}
