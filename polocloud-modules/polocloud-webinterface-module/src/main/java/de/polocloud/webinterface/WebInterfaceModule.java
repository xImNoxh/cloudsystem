package de.polocloud.webinterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;
import de.polocloud.webinterface.command.WebInterfaceCommand;
import de.polocloud.webinterface.config.WebInterfaceConfig;
import io.javalin.Javalin;

import java.io.File;

public class WebInterfaceModule extends Module {

    private WebInterfaceConfig config;
    private Javalin javalin;

    private static WebInterfaceModule instance;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onLoad() {
        instance = this;
        this.loadConfig();

        //change context ClassLoader to prevent Dependency-Collision
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WebInterfaceModule.class.getClassLoader());

        this.javalin = Javalin.create(config -> {
        });

        this.javalin.start(config.getPort());

        //change back to original ContextClassLoader
        Thread.currentThread().setContextClassLoader(classLoader);

        CloudAPI.getInstance().getCommandPool().registerCommand(new WebInterfaceCommand());
    }

    private void loadConfig() {
        File configPath = new File("modules/WebInterface/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }
        configPath = new File(configPath + "/users/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }

        File configFile = new File("modules/WebInterface/config.json");
        this.config = getConfigLoader().load(WebInterfaceConfig.class, configFile);

        getConfigSaver().save(this.config, configFile);

    }

    @Override
    public boolean onReload() {
        return true;
    }

    @Override
    public void onShutdown() {
        this.javalin.stop();
    }

    public static WebInterfaceModule getInstance() {
        return instance;
    }
}
