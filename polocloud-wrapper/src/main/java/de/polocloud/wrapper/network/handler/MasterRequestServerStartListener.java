package de.polocloud.wrapper.network.handler;

import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.master.MasterRequestServerStartPacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.config.WrapperConfig;
import de.polocloud.wrapper.config.properties.BungeeProperties;
import de.polocloud.wrapper.process.ProcessManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MasterRequestServerStartListener extends IPacketHandler<Packet> {

    private Executor executor = Executors.newCachedThreadPool();
    private WrapperConfig config;
    private ProcessManager processManager;

    private IConfigSaver configSaver = new SimpleConfigSaver();

    public MasterRequestServerStartListener(WrapperConfig config, ProcessManager processManager) {
        this.config = config;
        this.processManager = processManager;
    }

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {

        MasterRequestServerStartPacket packet = (MasterRequestServerStartPacket) obj;
        String templateName = packet.getTemplate();
        int memory = packet.getMemory();
        int maxPlayers = packet.getMaxPlayers();
        long snowFlake = packet.getSnowflake();

        Logger.log(LoggerType.INFO, "Starting " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
            packet.getServerName() + ConsoleColors.GRAY.getAnsiCode() + " server with template " + templateName + " (#" + snowFlake + ")");

        File serverFile = new File("storage/version/" + packet.getVersion().getTitle() + ".jar");
        if (!serverFile.exists()) {
            Logger.log(LoggerType.INFO, "Downloading follwing jar... (" + packet.getVersion().getTitle() + ")...");
            serverFile.getParentFile().mkdirs();
            try {
                FileUtils.copyURLToFile(new URL(packet.getVersion().getUrl()), serverFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Logger.log(LoggerType.INFO, "Downloading " + ConsoleColors.GREEN.getAnsiCode() + "successfully " + ConsoleColors.GRAY.getAnsiCode() + "completed.");
        }

        createDefaultTemplateDirectory(templateName);

        if (packet.isStatic()) {
            executor.execute(() -> handleStaticServerStart(packet.getServerName(), packet.getSnowflake(), serverFile, packet.getMemory(), packet.getMaxPlayers()));
        } else {
            //handle dynamic servers
            executor.execute(() -> handleDynamicServerStart(templateName, snowFlake, packet.isProxy(), serverFile, memory, maxPlayers, packet.getServerName(), packet.getMotd()));
        }

    }

    public void handleStaticServerStart(String serverName, long snowflake, File serverFile, int maxMemory, int maxPlayers) {
        File serverDirectory = new File("static/" + serverName + "#" + +snowflake);

        try {
            //copy server.jar and api to server directory
            FileUtils.copyFile(serverFile, new File(serverDirectory + "/spigot.jar"));
            FileUtils.copyFile(new File("templates/PoloCloud-API.jar"), new File(serverDirectory + "/plugins/PoloCloud-API.jar"));

            //config
            File poloCloudConfigFile = new File(serverDirectory + "/PoloCloud.json");
            FileUtils.writeStringToFile(poloCloudConfigFile, "{\"Master-Address\": \"" + config.getMasterAddress() + "\"}");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //save port and server in config; load on wrapper start
        int port = generatePort();

        config.getStaticServers().add(serverName + "#" + snowflake + "," + port + "," + maxMemory);
        configSaver.save(config, new File("config.json"));

        ProcessBuilder processBuilder = new ProcessBuilder(("java -jar -Xms" + maxMemory + "M -Xmx" + maxMemory + "M -Dcom.mojang.eula.agree=true spigot.jar nogui --online-mode false --max-players " + maxPlayers + " --noconsole --port " + port).split(" "));
        try {
            processBuilder.directory(serverDirectory);

            Process process = processBuilder.start();
            processManager.addProcess(snowflake, process);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createDefaultTemplateDirectory(String templateName) {
        File file = new File("templates/" + templateName + "/plugins");
        file.mkdirs();
    }


    private void handleDynamicServerStart(String templateName, long snowflake, boolean isProxy, File poloCloudFile, int maxMemory, int maxPlayers, String serverName, String motd) {
        File serverDirectory = new File("tmp/" + serverName + "#" + snowflake);

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
                new BungeeProperties(serverDirectory, maxPlayers, 25565, motd);
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
            Logger.log(LoggerType.INFO, "Starting " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + serverName + ConsoleColors.GRAY.getAnsiCode() + " on default port...");

        } else {
            int port = generatePort();
            processBuilder = new ProcessBuilder(("java -jar -Xms" + maxMemory + "M -Xmx" + maxMemory + "M -Dcom.mojang.eula.agree=true spigot.jar nogui --online-mode false --max-players " + maxPlayers + " --noconsole --port " + port).split(" "));
            Logger.log(LoggerType.INFO, "Starting " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + serverName + ConsoleColors.GRAY.getAnsiCode() + " on port " + port + "...");

        }

        processBuilder.directory(serverDirectory);

        Process process = null;
        try {
            process = processBuilder.start();
            processManager.addProcess(snowflake, process);

            process.waitFor();

            //FileUtils.deleteDirectory(serverDirectory);
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
    public Class<? extends Packet> getPacketClass() {
        return MasterRequestServerStartPacket.class;
    }
}
