package de.polocloud.api.template.helper;

import java.io.Serializable;
import java.util.Arrays;

public enum GameServerVersion implements Serializable {

    PROXY("https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar", "BungeeCord", TemplateType.PROXY),
    WATERFALL("https://papermc.io/api/v2/projects/waterfall/versions/1.17/builds/448/downloads/waterfall-1.17-448.jar", "Waterfall", TemplateType.PROXY),

    PAPERSPIGOT_1_16_5("https://papermc.io/api/v2/projects/paper/versions/1.16.5/builds/484/downloads/paper-1.16.5-484.jar", "Paper-1.16.5", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_15_2("https://papermc.io/api/v2/projects/paper/versions/1.15.2/builds/391/downloads/paper-1.15.2-391.jar", "Paper-1.15.2", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_14_4("https://papermc.io/api/v2/projects/paper/versions/1.14.4/builds/243/downloads/paper-1.14.4-243.jar", "Paper-1.14.4", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_13_2("https://papermc.io/api/v2/projects/paper/versions/1.13.2/builds/655/downloads/paper-1.13.2-655.jar", "Paper-1.13.2", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_12_2("https://papermc.io/api/v2/projects/paper/versions/1.12.2/builds/1618/downloads/paper-1.12.2-1618.jar", "Paper-1.12.2", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_11_2("https://papermc.io/api/v2/projects/paper/versions/1.11.2/builds/1104/downloads/paper-1.11.2-1104.jar", "Paper-1.11.2", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_10_2("https://papermc.io/api/v2/projects/paper/versions/1.10.2/builds/916/downloads/paper-1.10.2-916.jar", "Paper-1.10.2", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_9_4("https://papermc.io/api/v2/projects/paper/versions/1.9.4/builds/773/downloads/paper-1.9.4-773.jar", "Paper-1.9.4", TemplateType.MINECRAFT),
    PAPERSPIGOT_1_8_8("https://papermc.io/api/v2/projects/paper/versions/1.8.8/builds/443/downloads/paper-1.8.8-443.jar", "Paper-1.8.8", TemplateType.MINECRAFT),

    SPIGOT_1_16_5("https://cdn.getbukkit.org/spigot/spigot-1.16.5.jar", "Spigot-1.16.5", TemplateType.MINECRAFT),
    SPIGOT_1_16_4("https://cdn.getbukkit.org/spigot/spigot-1.16.4.jar", "Spigot-1.16.4", TemplateType.MINECRAFT),
    SPIGOT_1_16_3("https://cdn.getbukkit.org/spigot/spigot-1.16.3.jar", "Spigot-1.16.3", TemplateType.MINECRAFT),
    SPIGOT_1_16_2("https://cdn.getbukkit.org/spigot/spigot-1.16.2.jar", "Spigot-1.16.2", TemplateType.MINECRAFT),
    SPIGOT_1_16_1("https://cdn.getbukkit.org/spigot/spigot-1.16.1.jar", "Spigot-1.16.1", TemplateType.MINECRAFT),
    SPIGOT_1_15_2("https://cdn.getbukkit.org/spigot/spigot-1.15.2.jar", "Spigot-1.15.2", TemplateType.MINECRAFT),
    SPIGOT_1_15_1("https://cdn.getbukkit.org/spigot/spigot-1.15.1.jar", "Spigot-1.15.1", TemplateType.MINECRAFT),
    SPIGOT_1_14_4("https://cdn.getbukkit.org/spigot/spigot-1.14.4.jar", "Spigot-1.14.4", TemplateType.MINECRAFT),
    SPIGOT_1_14_3("https://cdn.getbukkit.org/spigot/spigot-1.14.3.jar", "Spigot-1.14.3", TemplateType.MINECRAFT),
    SPIGOT_1_14_2("https://cdn.getbukkit.org/spigot/spigot-1.14.2.jar", "Spigot-1.14.2", TemplateType.MINECRAFT),
    SPIGOT_1_14("https://cdn.getbukkit.org/spigot/spigot-1.14.jar", "Spigot-1.14", TemplateType.MINECRAFT),
    SPIGOT_1_13_2("https://cdn.getbukkit.org/spigot/spigot-1.13.2.jar", "Spigot-1.13.2", TemplateType.MINECRAFT),
    SPIGOT_1_13_1("https://cdn.getbukkit.org/spigot/spigot-1.13.1.jar", "Spigot-1.13.1", TemplateType.MINECRAFT),
    SPIGOT_1_13("https://cdn.getbukkit.org/spigot/spigot-1.13.jar", "Spigot-1.13", TemplateType.MINECRAFT),
    SPIGOT_1_12_2("https://cdn.getbukkit.org/spigot/spigot-1.12.2.jar", "Spigot-1.12.2", TemplateType.MINECRAFT),
    SPIGOT_1_12_1("https://cdn.getbukkit.org/spigot/spigot-1.12.1.jar", "Spigot-1.12.1", TemplateType.MINECRAFT),
    SPIGOT_1_12("https://cdn.getbukkit.org/spigot/spigot-1.12.jar", "Spigot-1.12", TemplateType.MINECRAFT),
    SPIGOT_1_11_2("https://cdn.getbukkit.org/spigot/spigot-1.11.2.jar", "Spigot-1.11.2", TemplateType.MINECRAFT),
    SPIGOT_1_11_1("https://cdn.getbukkit.org/spigot/spigot-1.11.1.jar", "Spigot-1.11.1", TemplateType.MINECRAFT),
    SPIGOT_1_11("https://cdn.getbukkit.org/spigot/spigot-1.11.jar", "Spigot-1.11", TemplateType.MINECRAFT),
    SPIGOT_1_10_2("https://cdn.getbukkit.org/spigot/spigot-1.10.2-R0.1-SNAPSHOT-latest.jar", "Spigot-1.10.2", TemplateType.MINECRAFT),
    SPIGOT_1_10("https://cdn.getbukkit.org/spigot/spigot-1.10-R0.1-SNAPSHOT-latest.jar", "Spigot-1.10", TemplateType.MINECRAFT),
    SPIGOT_1_9_4("https://cdn.getbukkit.org/spigot/spigot-1.9.4-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.4", TemplateType.MINECRAFT),
    SPIGOT_1_9_2("https://cdn.getbukkit.org/spigot/spigot-1.9.2-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9.2", TemplateType.MINECRAFT),
    SPIGOT_1_9("https://cdn.getbukkit.org/spigot/spigot-1.9-R0.1-SNAPSHOT-latest.jar", "Spigot-1.9", TemplateType.MINECRAFT),
    SPIGOT_1_8_8("https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar", "Spigot-1.8.8", TemplateType.MINECRAFT),
    ;

    private final TemplateType templateType;
    private final String title;
    private final String url;

    GameServerVersion(String url, String title, TemplateType templateType) {
        this.title = title;
        this.url = url;
        this.templateType = templateType;
    }

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

    public String getTitle() {
        return title;
    }

    public TemplateType getTemplateType() {
        return templateType;
    }

    public String getUrl() {
        return url;
    }

}
