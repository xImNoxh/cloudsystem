package de.polocloud.updater;

import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateServer {

    public static void main(String[] args) {

        IConfigLoader configLoader = new SimpleConfigLoader();
        IConfigSaver configSaver = new SimpleConfigSaver();

        File bootstrapFile = new File("bootstrap.jar");
        File apiFile = new File("PoloCloud-API.jar");

        File configFile = new File("config.json");

        AtomicReference<UpdateConfig> config = new AtomicReference<>(configLoader.load(UpdateConfig.class, configFile));

        Javalin javalin = Javalin.create();

        javalin.get("/updater/download/bootstrap", context -> {
            context.result(new FileInputStream(bootstrapFile));
            config.get().addBootstrapDownloadCount();
            configSaver.save(config.get(), configFile);

        });
        javalin.get("/updater/version/bootstrap", context -> {
            config.set(configLoader.load(UpdateConfig.class, configFile));
            context.result("{\"currentVersion\": \"" + config.get().getBootstrapVersion()+ "\"}");
        });

        javalin.get("/updater/download/api", context -> {
            context.result(new FileInputStream(apiFile));
            config.get().addApiDownloadCount();
            configSaver.save(config.get(), configFile);

        });
        javalin.get("/updater/version/api", context -> {
            config.set(configLoader.load(UpdateConfig.class, configFile));
            context.result("{\"currentVersion\": \"" + config.get().getApiVersion() + "\"}");
        });

        javalin.get("/stats", context -> {

            context.json(config.get());
        });

        javalin.start(8870);

    }

}
