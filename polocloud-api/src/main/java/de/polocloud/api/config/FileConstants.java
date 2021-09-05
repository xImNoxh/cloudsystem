package de.polocloud.api.config;

import de.polocloud.api.template.base.ITemplate;

import java.io.File;

public class FileConstants {

    //File names
    public static final String CLOUD_API_NAME = "PoloCloud-API.jar";
    public static final String CLOUD_JSON_NAME = "PoloCloud.json";

    //The template directory
    public static final File WRAPPER_TEMPLATES = new File("templates/");
    public static final File WRAPPER_CLOUD_API = new File(WRAPPER_TEMPLATES, CLOUD_API_NAME);
    public static final File WRAPPER_EVERY_TEMPLATE = new File(WRAPPER_TEMPLATES, "EVERY_SERVER");
    public static final File WRAPPER_EVERY_MINECRAFT_TEMPLATE = new File(WRAPPER_TEMPLATES, "EVERY_SPIGOT");
    public static final File WRAPPER_EVERY_PROXY_TEMPLATE = new File(WRAPPER_TEMPLATES, "EVERY_PROXY");
    public static final File WRAPPER_DYNAMIC_SERVERS = new File("tmp/");
    public static final File WRAPPER_STATIC_SERVERS = new File("static/");
    public static final File WRAPPER_STORAGE = new File("storage/");
    public static final File WRAPPER_STORAGE_VERSIONS = new File(WRAPPER_STORAGE, "version/");
    public static final File WRAPPER_CONFIG_FILE = new File("config.json");

    //Global stuff
    public static final File GLOBAL_FOLDER = new File("global/");
    public static final File TEMP_FILES = new File(GLOBAL_FOLDER, "tempFiles/");
    public static final File DATABASE_FOLDER = new File(GLOBAL_FOLDER, "db/");
    public static final File POLO_LOGS_FOLDER = new File(GLOBAL_FOLDER, "logs/");
    public static final File LAUNCHER_FILE = new File("launcher.json");
    public static final File BOOTSTRAP_FILE = new File("bootstrap.json");
    public static final File OLD_BOOTSTRAP_FILE = new File("old-bootstrap.json");


    //Master stuff
    public static final File MASTER_MODULES = new File("modules/");
    public static final File MASTER_CONFIG_FILE = new File("config.json");
    public static final File MASTER_TEMPLATE_INFO = new File("templates/");
    public static final File MASTER_PLAYER_PROPERTIES = new File(GLOBAL_FOLDER, "properties");

    //Logger
    public static final File LOGGER_FOLDER = new File(GLOBAL_FOLDER, "oldLogger/");
    public static final String LOGGER_LATEST_NAME = "latest.log";
}
