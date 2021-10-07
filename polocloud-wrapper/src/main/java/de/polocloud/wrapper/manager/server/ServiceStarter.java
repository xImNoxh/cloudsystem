package de.polocloud.wrapper.manager.server;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.network.packets.gameserver.GameServerUnregisterPacket;
import de.polocloud.api.network.packets.wrapper.WrapperServerStoppedPacket;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.properties.BungeeProperties;
import de.polocloud.wrapper.impl.config.properties.SpigotExtraProperties;
import de.polocloud.wrapper.impl.config.properties.SpigotProperties;
import de.polocloud.wrapper.impl.config.properties.VelocityProperties;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.impl.SimpleScreen;
import de.polocloud.wrapper.version.VersionInstaller;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ServiceStarter {

    private final IGameServer service;
    private final ITemplate template;

    private final File serverFile;
    private final File serverLocation;

    private final boolean installed;

    public ServiceStarter(IGameServer service) {
        this.service = service;
        this.template = service.getTemplate();

        GameServerVersion version = template.getVersion();
        if (version == null) {
            if (template.getTemplateType() == TemplateType.PROXY) {
                version = GameServerVersion.BUNGEE;
            } else {
                version = GameServerVersion.SPIGOT_1_8_8;
            }
        }

        this.serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
        if (!serverFile.exists()) {
            installed = VersionInstaller.installVersion(template.getVersion());
        }else{
            installed = true;
        }


        File location;

        if (template.isDynamic()) {
            location = new File(FileConstants.WRAPPER_DYNAMIC_SERVERS, service.getName() + "#" + service.getSnowflake());
        } else {
            location = new File(FileConstants.WRAPPER_STATIC_SERVERS, service.getName());
        }

        this.serverLocation = location;

        this.serverLocation.mkdirs();
        FileConstants.WRAPPER_EVERY_MINECRAFT_TEMPLATE.mkdirs();
        FileConstants.WRAPPER_EVERY_PROXY_TEMPLATE.mkdirs();
        FileConstants.WRAPPER_EVERY_TEMPLATE.mkdirs();

        new File(FileConstants.WRAPPER_EVERY_MINECRAFT_TEMPLATE, "plugins/").mkdirs();
        new File(FileConstants.WRAPPER_EVERY_PROXY_TEMPLATE, "plugins/").mkdirs();
        new File(FileConstants.WRAPPER_EVERY_TEMPLATE, "plugins/").mkdirs();
    }

    /**
     * Checks if this wrapper is allowed to start this server
     */
    public boolean checkWrapper() {
        IWrapper wrapper = Wrapper.getInstance();

        boolean allow = true;

        if (template.getServers().size() > template.getMaxServerCount()) {
            PoloCloudAPI.getInstance().messageCloud("§7Wrapper §6" + wrapper.getName() + " §7could not start §e" + service.getName() + " §7because the maximum servers allowed for template §b" + template.getName() + " §7is currently §3" + template.getMaxServerCount() + "§7!");
            allow = false;
        } else {

            if (wrapper.getMaxSimultaneouslyStartingServices() > 0 && wrapper.getCurrentlyStartingServices() >= wrapper.getMaxSimultaneouslyStartingServices()) {
                if (!wrapper.hasEnoughMemory(service.getTotalMemory())) {
                    PoloCloudAPI.getInstance().messageCloud("§7Wrapper §6" + wrapper.getName() + " §7wasn't able to start §e" + service.getName() + " §7because it is starting §cmore §7servers at once §cthan allowed §7and also it does §cnot §7have enough §6memory §7to start it!");
                } else {
                    PoloCloudAPI.getInstance().messageCloud("§7Wrapper §6" + wrapper.getName() + " §7wasn't able to start §e" + service.getName() + " §7because it is starting §cmore §7servers at once §cthan allowed§7!");
                    if (Wrapper.getInstance().getConfig().isAddCancelledServicesToQueue()) {
                        PoloCloudAPI.getInstance().messageCloud("§7Wrapper §6" + wrapper.getName() + " §7added §e" + service.getName() + " §7to queue!");
                        Scheduler.runtimeScheduler().schedule(() -> Wrapper.getInstance().startServer(service), () -> wrapper.getCurrentlyStartingServices() < wrapper.getMaxSimultaneouslyStartingServices());
                    }
                }
                allow = false;
            } else if (!wrapper.hasEnoughMemory(service.getTotalMemory())) {
                PoloCloudAPI.getInstance().messageCloud("§7Wrapper §6" + wrapper.getName() + " §7does §cnot §7have enough §6memory §7to start §e" + service.getName() + "§7!");
                allow = false;
            }
        }

        if (!allow) {
            serverLocation.delete();
            PoloCloudAPI.getInstance().getGameServerManager().unregister(service);
            PoloCloudAPI.getInstance().sendPacket(new GameServerUnregisterPacket(service.getSnowflake(), service.getName()));
        } else {
            wrapper.setCurrentlyStartingServices((wrapper.getCurrentlyStartingServices() + 1));
            wrapper.update();
        }

        return allow;
    }

    /**
     * Copies all files from the template
     * to the temp directory
     */
    public void copyFiles() throws Exception {

        File templateDirectory = new File(FileConstants.WRAPPER_TEMPLATES, template.getName() + "/");
        File pluginsDirectory = new File(templateDirectory, "/plugins");

        if (!pluginsDirectory.exists()) {
            pluginsDirectory.mkdirs();
        }


        FileUtils.copyDirectory(templateDirectory, serverLocation);

        try {
            FileUtils.copyFile(this.serverFile, new File(serverLocation, getJarFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : Wrapper.getInstance().getModuleCopyService().getCachedModule().keySet()) {
            List<ModuleCopyType> types = Arrays.asList(Wrapper.getInstance().getModuleCopyService().getCachedModule().get(file));
            if(types.contains(ModuleCopyType.ALL)){
                FileUtils.copyFile(file, new File(serverLocation + "/plugins/", file.getName()));
            }else if(types.contains(ModuleCopyType.PROXIES) && template.getTemplateType().equals(TemplateType.PROXY)){
                FileUtils.copyFile(file, new File(serverLocation + "/plugins/", file.getName()));
            }else if(types.contains(ModuleCopyType.SPIGOT) && template.getTemplateType().equals(TemplateType.MINECRAFT)){
                FileUtils.copyFile(file, new File(serverLocation + "/plugins/", file.getName()));
            }else if(types.contains(ModuleCopyType.LOBBIES) && template.isLobby()){
                FileUtils.copyFile(file, new File(serverLocation + "/plugins/", file.getName()));
            }
        }

        //Global templates

        FileUtils.copyDirectory(FileConstants.WRAPPER_EVERY_TEMPLATE, serverLocation); //every template

        if (template.getTemplateType() == TemplateType.PROXY) {
            FileUtils.copyDirectory(FileConstants.WRAPPER_EVERY_PROXY_TEMPLATE, serverLocation); //Every proxy template
        } else {
            FileUtils.copyDirectory(FileConstants.WRAPPER_EVERY_MINECRAFT_TEMPLATE, serverLocation); //Every spigot template
        }


        if(new File(FileConstants.WRAPPER_TEMPLATES + "/" + FileConstants.CLOUD_API_NAME).exists()){
            FileUtils.copyFile(new File(FileConstants.WRAPPER_TEMPLATES + "/" + FileConstants.CLOUD_API_NAME), new File(serverLocation + "/plugins/" + FileConstants.CLOUD_API_NAME));
        }else{
            try {
                URL inputUrl = getClass().getResource("/" + FileConstants.CLOUD_API_NAME);
                FileUtils.copyURLToFile(inputUrl, new File(serverLocation + "/plugins/" + FileConstants.CLOUD_API_NAME));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File serverIcon = new File(serverLocation, "server-icon.png");

        if (service.getTemplate().getTemplateType() == TemplateType.PROXY && !serverIcon.exists()) {
            try {
                URL inputUrl = getClass().getResource("/server-icon.png");
                FileUtils.copyURLToFile(inputUrl, serverIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Creates the properties for the service
     */
    public void createProperties() throws Exception {

        File serverIcon = new File(new File(FileConstants.WRAPPER_TEMPLATES, template.getName() + "/"), "server-icon.png");
        if (serverIcon.exists()) {
            FileUtils.copyFile(serverIcon, new File(serverLocation, "server-icon.png"));
        }

        if (service.getTemplate().getTemplateType() == TemplateType.PROXY) {

            boolean onlineMode = PoloCloudAPI.getInstance().getMasterConfig().getProperties().isProxyOnlineMode();
            boolean proxyProtocol = PoloCloudAPI.getInstance().getMasterConfig().getProperties().isProxyPingForwarding();

            if (template.getVersion().getTitle().toLowerCase().contains("velocity")) {
                new VelocityProperties(serverLocation, template.getMaxPlayers(), service.getPort(), template.getMotd(), proxyProtocol, onlineMode);
            } else {
                new BungeeProperties(serverLocation, template.getMaxPlayers(), service.getPort(), template.getMotd(), proxyProtocol, onlineMode);
            }
        } else {
            FileWriter eula = null;
            try {
                eula = new FileWriter(serverLocation + "/eula.txt");
                eula.write("eula=true");
                eula.flush();
                eula.close();
            } catch (IOException ignored) {

            } finally {
                if (eula != null) {
                    try {
                        eula.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            new SpigotProperties(serverLocation, service.getPort(), template.getMotd(), template.getMaxPlayers(), service.getName());
            new SpigotExtraProperties(serverLocation, service.getPort());
        }
    }

    /**
     * Creates all the cloud files
     * containing information to identify services
     *
     * @throws Exception if something goes wrong
     */
    public void createCloudFiles() throws Exception{

        //config
        File poloCloudConfigFile = new File(serverLocation + "/" + FileConstants.CLOUD_JSON_NAME);

        JsonData jsonData = new JsonData(poloCloudConfigFile);

        jsonData.append("Master-Address", Wrapper.getInstance().getConfig().getMasterAddress());
        jsonData.append("GameServer-Name", service.getName());
        jsonData.append("GameServer-Snowflake", service.getSnowflake());
        jsonData.append("port", service.getPort());

        jsonData.save();

    }

    /**
     * Gets the jarFile-name depending on the type
     * (Proxy or Spigot)
     *
     * @return string file name
     */
    public String getJarFile() {
        return this.template.getTemplateType() == TemplateType.PROXY ? "proxy.jar" : "spigot.jar";
    }

    /**
     * Starts the service finally
     * (Using some lines of code by CryCodes for me)
     *
     * @throws Exception if something goes wrong
     */
    public void start(Consumer<IGameServer> consumer) throws Exception {

        String[] command = new String[]{
            "java",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=50",
            "-XX:-UseAdaptiveSizePolicy",
            "-XX:CompileThreshold=100",
            "-Dcom.mojang.eula.agree=true",
            "-Dio.netty.recycler.maxCapacity=0",
            "-Dio.netty.recycler.maxCapacity.default=0",
            "-Djline.terminal=jline.UnsupportedTerminal",
            "-Xmx" + template.getMaxMemory() + "M",
            "-jar",
            getJarFile(),
            service.getTemplate().getTemplateType() == TemplateType.MINECRAFT ? "nogui" : ""
        };

        Scheduler.runtimeScheduler().schedule(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(command).directory(serverLocation);
                Process process = processBuilder.start();

                if (process.isAlive()) {

                    IWrapper wrapper = Wrapper.getInstance();
                    wrapper.setCurrentlyStartingServices((wrapper.getCurrentlyStartingServices() - 1));
                    wrapper.update();

                    //Registering screen
                    IScreen screen = new SimpleScreen(Thread.currentThread(), process, serverLocation, service.getSnowflake(), service.getName());
                    Wrapper.getInstance().getScreenManager().registerScreen(service.getName(), screen);
                    screen.start();

                    consumer.accept(service);

                    //Waiting for process to stop
                    process.waitFor();

                    //Stopped
                    ServiceStopper serviceStopper = new ServiceStopper(service);
                    serviceStopper.stop(iGameServer -> {
                        PoloCloudAPI.getInstance().sendPacket(new WrapperServerStoppedPacket(iGameServer.getName(), iGameServer.getSnowflake()));

                        Wrapper.getInstance().getScreenManager().unregisterScreen(iGameServer.getName());

                        //Check if removed from cache between stopping and accepting consumer
                        PoloLogger.print(LogLevel.INFO, "§7Server §3" + iGameServer.getName() + "§7#§3" + iGameServer.getSnowflake() + " §7was §cstopped§7!");
                    });

                    return;
                }
                throw new IllegalStateException("Process terminated itself or couldn't be started");
            } catch (Exception exception) {
                exception.printStackTrace();
                PoloLogger.print(LogLevel.ERROR, "An exception was caught while starting a process for the gameserver: " + service.getName() + ". (" + exception.getMessage() + "). This server may hasn't correctly started.");
                PoloCloudAPI.getInstance().reportException(exception);
            }
        });
    }

    public boolean isInstalled() {
        return installed;
    }
}
