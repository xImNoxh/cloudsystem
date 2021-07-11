package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.config.WrapperConfig;
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
    private WrapperConfig config;

    public MasterRequestServerStartListener(WrapperConfig config) {

        this.config = config;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, IPacket obj) {

        MasterRequestServerStartPacket packet = (MasterRequestServerStartPacket) obj;
        String templateName = packet.getTemplate();
        int memory = packet.getMemory();
        int maxPlayers = packet.getMaxPlayers();
        long snowFlake = packet.getSnowflake();

        Logger.log(LoggerType.INFO, "starting server with template: " + templateName + " / " + snowFlake);

        File serverFile = new File("storage/version/" + packet.getVersion().getTitle() + ".jar");
        if (!serverFile.exists()) {
            Logger.log(LoggerType.INFO, "downloading follwing jar (" + packet.getVersion().getTitle() + ")...");
            serverFile.getParentFile().mkdirs();
            try {
                FileUtils.copyURLToFile(new URL(packet.getVersion().getUrl()), serverFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.log(LoggerType.INFO, "done");
        }

        createDefaultTemplateDirectory(templateName);

        executor.execute(() -> handleServerStart(templateName, snowFlake, packet.isProxy(), serverFile, memory, maxPlayers));

    }

    private void createDefaultTemplateDirectory(String templateName) {
        File file = new File("templates/" + templateName + "/plugins");
        file.mkdirs();
    }


    private void handleServerStart(String templateName, long snowflake, boolean isProxy, File poloCloudFile, int maxMemory, int maxPlayers) {
        File serverDirectory = new File("tmp/" + snowflake);

        try {
            //copy server.jar and api to server directory
            FileUtils.copyDirectory(new File("templates/" + templateName), serverDirectory);
            FileUtils.copyFile(poloCloudFile, new File(serverDirectory + (isProxy ? "/proxy.jar" : "/spigot.jar")));
            FileUtils.copyFile(new File("templates/PoloCloud-API.jar"), new File(serverDirectory + "/plugins/PoloCloud-API.jar"));

            //create spigot config - disable online-mode and enable bungeecord
            if (!isProxy) {
                File serverProp = new File(serverDirectory + "/server.properties");
                File spigotYML = new File(serverDirectory + "/spigot.yml");

                FileUtils.writeStringToFile(serverProp, "online-mode=false\nmotd=PoloCloud\n");

                FileUtils.writeStringToFile(spigotYML, "settings:\n  bungeecord: true\n");
            } else {
                File config = new File(serverDirectory + "/config.yml");
                FileUtils.writeStringToFile(config, "ip_forward: true\n");


                // FileUtils.writeStringToFile(config, "listeners:\n max_players: " + maxPlayers + "\n");
            }

            File poloCloudConfigFile = new File(serverDirectory + "/PoloCloud.json");
            FileUtils.writeStringToFile(poloCloudConfigFile, "{\"Master-Address\": \"" + config.getMasterAddress() + "\"}");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //start spigot.jar on random port
        ProcessBuilder processBuilder;
        if (isProxy) {
            processBuilder = new ProcessBuilder(("java -jar -Xms" + maxMemory + "M -Xmx" + maxMemory + "M proxy.jar").split(" "));
            Logger.log(LoggerType.INFO, "Starting server on " + ConsoleColors.GREEN.getAnsiCode() + "default " + ConsoleColors.GRAY.getAnsiCode() + "port");

        } else {
            int port = generatePort();
            processBuilder = new ProcessBuilder(("java -jar -Xms" + maxMemory + "M -Xmx" + maxMemory + "M -Dcom.mojang.eula.agree=true spigot.jar --online-mode false --max-players " + maxPlayers + " --noconsole --port " + port).split(" "));
            Logger.log(LoggerType.INFO, "Starting server on port " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + port);

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
