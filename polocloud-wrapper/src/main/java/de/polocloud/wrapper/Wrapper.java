package de.polocloud.wrapper;

import com.esotericsoftware.kryonetty.network.ConnectEvent;
import com.esotericsoftware.kryonetty.network.ReceiveEvent;
import com.esotericsoftware.kryonetty.network.handler.NetworkHandler;
import com.esotericsoftware.kryonetty.network.handler.NetworkListener;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.guice.PoloAPIGuiceModule;
import de.polocloud.api.network.IStartable;
import de.polocloud.api.network.ITerminatable;
import de.polocloud.api.network.client.SimpleNettyClient;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.StartServerPacket;
import de.polocloud.api.network.protocol.packet.TestPacket;
import de.polocloud.wrapper.commands.StopCommand;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Wrapper implements IStartable, ITerminatable {

    private CloudAPI cloudAPI;

    private SimpleNettyClient nettyClient;
    private final Executor executor = Executors.newCachedThreadPool();

    public Wrapper() {
        this.cloudAPI = new PoloCloudAPI(new PoloAPIGuiceModule());

        CloudAPI.getInstance().getCommandPool().registerCommand(new StopCommand());
    }

    @Override
    public void start() {
        this.nettyClient = this.cloudAPI.getGuice().getInstance(SimpleNettyClient.class);
        this.nettyClient.start();

        this.nettyClient.registerListener(new NetworkListener() {

            @NetworkHandler
            public void handle(ConnectEvent event) {
                System.out.println("connected");
            }

            @NetworkHandler
            public void handle(ReceiveEvent event) {

                if (event.getObject() instanceof IPacket) {
                    IPacket packet = (IPacket) event.getObject();

                    if (packet instanceof StartServerPacket) {
                        StartServerPacket ssp = (StartServerPacket) packet;

                        String templateName = ssp.getTemplateName();

                        executor.execute(() -> handleServerStart(templateName));

                    }

                }

            }

        });
        Wrapper.this.nettyClient.sendPacket(new TestPacket("Hallo Welt"));
    }

    private void handleServerStart(String templateName) {
        File serverDirectory = new File("tmp/" + UUID.randomUUID());

        try {
            FileUtils.copyDirectory(new File("templates/" + templateName), serverDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //start spigot.jar on random port
        int port = generatePort();
        ProcessBuilder processBuilder = new ProcessBuilder(("java -jar -Dcom.mojang.eula.agree=true spigot.jar --max-players 100 --noconsole --port " + port).split(" "));

        System.out.println("start server on port " + port);

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
    public boolean terminate() {
        return this.nettyClient.terminate();
    }
}
