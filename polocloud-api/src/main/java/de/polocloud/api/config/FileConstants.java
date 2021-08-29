package de.polocloud.api.config;

import de.polocloud.api.template.base.ITemplate;

import java.io.File;

public class FileConstants {

    //The cloudapi name
    public static final String CLOUD_API_NAME = "PoloCloud-API.jar";

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

    public static File getTemplateDirectory(ITemplate template) {
        return new File(WRAPPER_TEMPLATES, template.getName() + "/");
    }
}
