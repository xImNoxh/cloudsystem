package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.template.GameServerVersion;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
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

        File serverFile = new File("storage/version/" + packet.getVersion().getTitle() + ".jar");
        if (!serverFile.exists()) {
            System.out.println("downloading " + packet.getVersion().getTitle() + "....");
            serverFile.getParentFile().mkdirs();
            try {
                FileUtils.copyURLToFile(new URL(packet.getVersion().getUrl()), serverFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("done.");
        }

        createDefaultTemplateDirectory(templateName);

        executor.execute(() -> handleServerStart(templateName, snowFlake, packet.isProxy(), serverFile));

    }

    private void createDefaultTemplateDirectory(String templateName) {
        File file = new File("templates/" + templateName + "/plugins");
        file.mkdirs();
    }


    private void handleServerStart(String templateName, long snowflake, boolean isProxy, File serverFile) {
        File serverDirectory = new File("tmp/" + snowflake);

        try {
            //copy server.jar and api to server directory
            FileUtils.copyDirectory(new File("templates/" + templateName), serverDirectory);
            FileUtils.copyFile(serverFile, new File(serverDirectory + (isProxy ? "/proxy.jar" : "/spigot.jar")));
            FileUtils.copyFile(new File("templates/PoloCloud-API.jar"), new File(serverDirectory + "/plugins/PoloCloud-API.jar"));

            //create spigot config - disable online-mode and enable bungeecord
            if (!isProxy) {
                File serverProp = new File(serverDirectory + "/server.properties");
                File spigotYML = new File(serverDirectory + "/spigot.yml");

                FileUtils.writeStringToFile(serverProp, "online-mode=false\nmotd=PoloCloud\n");

                FileUtils.writeStringToFile(spigotYML, "settings:\n  bungeecord: true\n");
            }else{
                File config = new File(serverDirectory + "/config.yml");
                FileUtils.writeStringToFile(config, "ip_forward: true\n");

            }
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
            processBuilder = new ProcessBuilder(("java -jar -Dcom.mojang.eula.agree=true spigot.jar --online-mode false --max-players 100 --noconsole --port " + port).split(" "));
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
