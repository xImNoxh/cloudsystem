package de.polocloud.updater;

import io.javalin.Javalin;

import java.io.File;
import java.io.FileInputStream;

public class UpdateServer {

    public static void main(String[] args) {

        File downloadFile = new File("bootstrap.jar");

        Javalin javalin = Javalin.create();

        javalin.get("/updater/download/bootstrap", context -> context.result(new FileInputStream(downloadFile)));

        javalin.get("/updater/version/", context -> context.result("{\"currentVersion\": \"0.1.Alpha\"}"));

        javalin.start(8870);

    }

}
