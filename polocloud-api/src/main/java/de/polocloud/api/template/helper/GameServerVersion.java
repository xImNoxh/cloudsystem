package de.polocloud.api.template.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;

@AllArgsConstructor @Getter
public enum GameServerVersion implements Serializable {

    //Other bungeecords
    BUNGEE("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", "BungeeCord", TemplateType.PROXY, -1),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.17/builds/448/downloads/waterfall-1.17-448.jar", "Waterfall", TemplateType.PROXY, -1),

    //Velocity
    VELOCITY_3_0_1("https://versions.velocitypowered.com/download/3.0.1.jar", "Velocity", TemplateType.PROXY, -1),

    //Paper-Spigot
    PAPERSPIGOT_1_16_5("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/484/downloads/paper-1.16.5-484.jar", "Paper-1.16.5", TemplateType.MINECRAFT, 754),
    PAPERSPIGOT_1_15_2("https://papermc.io/api/v2/projects/paper/versions/1.15.2/builds/391/downloads/paper-1.15.2-391.jar", "Paper-1.15.2", TemplateType.MINECRAFT, 578),
    PAPERSPIGOT_1_14_4("https://papermc.io/api/v2/projects/paper/versions/1.14.4/builds/243/downloads/paper-1.14.4-243.jar", "Paper-1.14.4", TemplateType.MINECRAFT, 498),
    PAPERSPIGOT_1_13_2("https://papermc.io/api/v2/projects/paper/versions/1.13.2/builds/655/downloads/paper-1.13.2-655.jar", "Paper-1.13.2", TemplateType.MINECRAFT, 404),
    PAPERSPIGOT_1_12_2("https://papermc.io/api/v2/projects/paper/versions/1.12.2/builds/1618/downloads/paper-1.12.2-1618.jar", "Paper-1.12.2", TemplateType.MINECRAFT, 340),
    PAPERSPIGOT_1_9_4("https://papermc.io/api/v2/projects/paper/versions/1.9.4/builds/773/downloads/paper-1.9.4-773.jar", "Paper-1.9.4", TemplateType.MINECRAFT, 110),
    PAPERSPIGOT_1_8_8("https://papermc.io/api/v2/projects/paper/versions/1.8.8/builds/443/downloads/paper-1.8.8-443.jar", "Paper-1.8.8", TemplateType.MINECRAFT, 47),

    //Normal Spigot
    SPIGOT_1_16_5("https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar", "Spigot-1.16.5", TemplateType.MINECRAFT, 754),
    SPIGOT_1_16_4("https://cdn.getbukkit.org/spigot/spigot-1.16.4.jar", "Spigot-1.16.4", TemplateType.MINECRAFT, 754),
    SPIGOT_1_16_3("https://cdn.getbukkit.org/spigot/spigot-1.16.3.jar", "Spigot-1.16.3", TemplateType.MINECRAFT, 753),
    SPIGOT_1_16_2("https://cdn.getbukkit.org/spigot/spigot-1.16.2.jar", "Spigot-1.16.2", TemplateType.MINECRAFT, 751),
    SPIGOT_1_16_1("https://cdn.getbukkit.org/spigot/spigot-1.16.1.jar", "Spigot-1.16.1", TemplateType.MINECRAFT, 736),
    SPIGOT_1_16("https://cdn.getbukkit.org/spigot/spigot-1.16.jar", "Spigot-1.16", TemplateType.MINECRAFT, 735),
    SPIGOT_1_15_2("https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar", "Spigot-1.15.2", TemplateType.MINECRAFT, 578),
    SPIGOT_1_15_1("https://cdn.getbukkit.org/spigot/spigot-1.15.1.jar", "Spigot-1.15.1", TemplateType.MINECRAFT, 575),
    SPIGOT_1_15("https://cdn.getbukkit.org/spigot/spigot-1.15.jar", "Spigot-1.15", TemplateType.MINECRAFT, 573),
    SPIGOT_1_14_4("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar", "Spigot-1.14.4", TemplateType.MINECRAFT, 498),
    SPIGOT_1_14_3("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar", "Spigot-1.14.3", TemplateType.MINECRAFT, 490),
    SPIGOT_1_14_2("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar", "Spigot-1.14.2", TemplateType.MINECRAFT, 485),
    SPIGOT_1_14("https://cdn.getbukkit.org/spigot/spigot-1.14.jar", "Spigot-1.14", TemplateType.MINECRAFT, 477),
    SPIGOT_1_13_2("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar", "Spigot-1.13.2", TemplateType.MINECRAFT, 404),
    SPIGOT_1_13_1("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar", "Spigot-1.13.1", TemplateType.MINECRAFT, 401),
    SPIGOT_1_13("https://cdn.getbukkit.org/spigot/spigot-1.13.jar", "Spigot-1.13", TemplateType.MINECRAFT, 393),
    SPIGOT_1_12_2("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar", "Spigot-1.12.2", TemplateType.MINECRAFT, 340),
    SPIGOT_1_12_1("https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar", "Spigot-1.12.1", TemplateType.MINECRAFT, 338),
    SPIGOT_1_12("https://cdn.getbukkit.org/spigot/spigot-1.12.jar", "Spigot-1.12", TemplateType.MINECRAFT, 335),
    SPIGOT_1_11_1("https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar", "Spigot-1.11.1", TemplateType.MINECRAFT, 316),
    SPIGOT_1_11("https://cdn.getbukkit.org/spigot/spigot-1.11.jar", "Spigot-1.11", TemplateType.MINECRAFT, 315),
    SPIGOT_1_10("https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar", "Spigot-1.10", TemplateType.MINECRAFT, 210),
    SPIGOT_1_9_4("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.4", TemplateType.MINECRAFT, 110),
    SPIGOT_1_9_2("https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.2", TemplateType.MINECRAFT, 109),
    SPIGOT_1_9("https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9", TemplateType.MINECRAFT, 107),
    SPIGOT_1_8_8("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", "Spigot-1.8.8", TemplateType.MINECRAFT, 47),
    ;

    /*

     MINECRAFT_1_17(755, "1.17");
     *
     */

    private String url;
    private String title;
    private TemplateType templateType;
    private int protocolId;

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
