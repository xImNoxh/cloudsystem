package de.polocloud.updater;

import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;

public class UpdateServer {

    public static void main(String[] args) {

        File bootstrapFile = new File("bootstrap.jar");
        File apiFile = new File("PoloCloud-API.jar");

        String bootstrapVersion = "0.0.1.Closed-Alpha";
        String apiVersion = "0.1.Closed-Alpha";


        Javalin javalin = Javalin.create();

        javalin.get("/updater/download/bootstrap", context -> context.result(new FileInputStream(bootstrapFile)));
        javalin.get("/updater/version/bootstrap", context -> context.result("{\"currentVersion\": \"" + bootstrapVersion + "\"}"));

        javalin.get("/updater/download/api", context -> context.result(new FileInputStream(apiFile)));
        javalin.get("/updater/version/api", context -> context.result("{\"currentVersion\": \"" + apiVersion + "\"}"));

        javalin.start(8870);

    }

}
