package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MasterRequestServerStartListener extends IPacketHandler {

    private Executor executor = Executors.newCachedThreadPool();

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        MasterRequestServerStartPacket packet = (MasterRequestServerStartPacket) obj;
        String templateName = packet.getTemplate();
        long snowFlake = packet.getSnowflake();
        System.out.println("starting server with template: " + templateName + " / " + snowFlake);

        executor.execute(() -> handleServerStart(templateName, snowFlake, packet.isProxy()));

    }


    private void handleServerStart(String templateName, long snowflake, boolean isProxy) {
        File serverDirectory = new File("tmp/" + snowflake);

        try {
            FileUtils.copyDirectory(new File("templates/" + templateName), serverDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start spigot.jar on random port
        ProcessBuilder processBuilder;
        if (isProxy) {
            processBuilder = new ProcessBuilder(("java -jar proxy.jar").split(" "));
            System.out.println("start server on default port");

        } else {
            int port = generatePort();
            processBuilder = new ProcessBuilder(("java -jar -Dcom.mojang.eula.agree=true spigot.jar --max-players 100 --noconsole --port " + port).split(" "));
            System.out.println("start server on port " + port);

        }

        processBuilder.directory(serverDirectory);

        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();

            FileUtils.deleteDirectory(serverDirectory);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private int generatePort() {
        int port = 0;
        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }


    @Override
    public Class<? extends IPacket> getPacketClass() {
        return MasterRequestServerStartPacket.class;
    }
}
