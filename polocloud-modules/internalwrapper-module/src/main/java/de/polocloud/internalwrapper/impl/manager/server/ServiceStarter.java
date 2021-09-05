package de.polocloud.internalwrapper.impl.manager.server;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.network.packets.wrapper.WrapperServerStoppedPacket;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.internalwrapper.InternalWrapper;
import de.polocloud.internalwrapper.impl.manager.screen.IScreen;
import de.polocloud.internalwrapper.impl.manager.screen.impl.SimpleScreen;
import de.polocloud.internalwrapper.utils.properties.BungeeProperties;
import de.polocloud.internalwrapper.utils.properties.SpigotExtraProperties;
import de.polocloud.internalwrapper.utils.properties.SpigotProperties;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ServiceStarter {


    private final IGameServer service;
    private final ITemplate template;

    private final File serverFile;
    private final File serverLocation;

    public ServiceStarter(IGameServer service) {

        this.service = service;
        this.template = service.getTemplate();

        this.serverFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, template.getVersion().getTitle() + ".jar");
        if (!serverFile.exists()) {
            PoloLogger.print(LogLevel.INFO, "§7Template of Type §b" + template.getTemplateType().getDisplayName() + " §7Requires following jar... (§3" + template.getVersion().getTitle() + ") §7Downloading...");
            serverFile.getParentFile().mkdirs();
            try {
                FileUtils.copyURLToFile(new URL(template.getVersion().getUrl()), serverFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PoloLogger.print(LogLevel.INFO, "Downloading §asuccessfully §7completed.");
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
        FileUtils.copyFile(serverFile, new File(serverLocation, getJarFile()));


        for (CloudModule module : PoloCloudAPI.getInstance().getModuleHolder().getModules()) {
            File file = module.getModuleFile();
            List<ModuleCopyType> types = Arrays.asList(module.info().copyTypes());
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
        if (!FileConstants.WRAPPER_CLOUD_API.exists()) {
            PoloLogger.print(LogLevel.ERROR, "§cCouldn't find §e" + FileConstants.CLOUD_API_NAME + "§c!");
            return;
        }
        FileUtils.copyFile(FileConstants.WRAPPER_CLOUD_API, new File(serverLocation + "/plugins/" + FileConstants.CLOUD_API_NAME));
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

            new BungeeProperties(serverLocation, template.getMaxPlayers(), service.getPort(), template.getMotd(), proxyProtocol, onlineMode);

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

        jsonData.append("Master-Address", "127.0.0.1:" + PoloCloudAPI.getInstance().getMasterConfig().getProperties().getPort());
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

                    //Registering screen
                    IScreen screen = new SimpleScreen(Thread.currentThread(), process, serverLocation, service.getSnowflake(), service.getName());
                    InternalWrapper.getInstance().getScreenManager().registerScreen(service.getName(), screen);
                    screen.start();

                    consumer.accept(service);

                    //Waiting for process to stop
                    process.waitFor();

                    //Stopped
                    ServiceStopper serviceStopper = new ServiceStopper(service);
                    serviceStopper.stop(iGameServer -> {
                        PoloCloudAPI.getInstance().sendPacket(new WrapperServerStoppedPacket(iGameServer.getName(), iGameServer.getSnowflake()));

                        //Check if removed from cache between stopping and accepting consumer
                        PoloLogger.print(LogLevel.INFO, "§7Server §3" + iGameServer.getName() + "§7#§3" + iGameServer.getSnowflake() + " §7was §cstopped§7!");
                    });

                    return;
                }
                throw new IllegalStateException("Process terminated itself or couldn't be started");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

}
