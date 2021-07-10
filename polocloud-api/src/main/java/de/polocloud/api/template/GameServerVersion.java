package de.polocloud.api.template;

public enum GameServerVersion {

    PROXY("BungeeCord", "https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar"),

    SPIGOT_1_8_8("Spigot-1.8.8","https://cdn.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar");

    private String title;
    private String url;

    GameServerVersion(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public static GameServerVersion getVersion(String title){
        for (GameServerVersion value : values()) {
            if(value.getTitle().equalsIgnoreCase(title)){
                return value;
            }
        }
        return null;
    }

}
