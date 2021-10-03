package de.polocloud.bootstrap.setup;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.master.MasterConfig;
import de.polocloud.api.config.master.properties.Properties;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.setup.FutureAnswer;
import de.polocloud.api.setup.Setup;
import de.polocloud.api.setup.SetupBuilder;
import de.polocloud.api.setup.Step;
import de.polocloud.api.setup.accepter.StepAcceptor;
import de.polocloud.api.setup.accepter.StepAnswer;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.console.ConsoleRunner;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.enumeration.ReportType;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MasterSetup extends StepAcceptor implements Setup {

    @Override
    public void sendSetup() {

        ConsoleRunner.getInstance().setActive(false);
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("On what port should the Master run?", isInteger());

        step.addStep("Should Player-Connections (Connecting / Disconnecting) be displayed in Console? (true/false)", isBoolean())
            .addStep("How many GameServers are allowed to start at the same time? (-1 for infinite)", isInteger())
            .addStep("Should Cracked-Players be allowed to join the network? (true/false)", isBoolean())
            .addStep("What information should be sent to our servers? (Mostly Exceptions) (NONE: No information will be sent to us, MINIMAL: Minimal information about a exception will be sent (stacktrace, version, master or wrapper), OPTIONAL: More information will be sent (Java-Version, stacktrace, version, master or wrapper)", new FutureAnswer() {
            @Override
            public Object[] findPossibleAnswers(SetupBuilder steps) {
                return Arrays.stream(ReportType.values()).toArray();
            }
            })
            .addStep("Should ProxyProtocol be enabled? (If you don't know what this is, type 'false' !)", isBoolean())
            .addStep("Should the online-players amount be synchronised on all online Proxies? (true/false)", isBoolean())
            .addStep("What is the start range for GameServers to start? (default: 3000)", isInteger())
            .addStep("Should a default Proxy- and Lobby-Template be created? (true/false)", isBoolean());

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {

                int port = steps.get(0).getAnswerAsInt();
                boolean logPlayerConnections = steps.get(1).getAnswerAsBoolean();
                int maxGameServersStart = steps.get(2).getAnswerAsInt();
                boolean onlineMode = !steps.get(3).getAnswerAsBoolean();
                ReportType reportType = ReportType.valueOf(steps.get(4).getAnswer().toUpperCase());
                boolean proxyProtocol = steps.get(5).getAnswerAsBoolean();
                boolean syncProxies = steps.get(6).getAnswerAsBoolean();
                int startPort = steps.get(7).getAnswerAsInt();
                boolean defaultGroups = steps.get(8).getAnswerAsBoolean();

                PoloCloudClient.getInstance().getClientConfig().setReportType(reportType);

                MasterConfig masterConfig = PoloCloudAPI.getInstance().getMasterConfig();
                Properties properties = masterConfig.getProperties();

                properties.setPort(port);
                properties.setLogPlayerConnections(logPlayerConnections);
                properties.setMaxSimultaneouslyStartingTemplates(maxGameServersStart);
                properties.setProxyOnlineMode(onlineMode);
                properties.setProxyPingForwarding(proxyProtocol);
                properties.setSyncProxyOnlinePlayers(syncProxies);
                properties.setDefaultServerStartPort(startPort);

                if (defaultGroups) {


                    ITemplateManager templateManager = PoloCloudAPI.getInstance().getTemplateManager();
                    ITemplate proxy = new SimpleTemplate("Proxy", false, 5, 1, TemplateType.PROXY, GameServerVersion.BUNGEE, 75, 750, false, "A PoloCloud Proxy", 100, new String[]{"Wrapper-1"});
                    ITemplate lobby = new SimpleTemplate("Lobby", false, 5, 1, TemplateType.MINECRAFT, GameServerVersion.SPIGOT_1_8_8, 30, 512, false, "A PoloCloud Lobby", 100, new String[]{"Wrapper-1"});

                    templateManager.addTemplate(proxy);
                    templateManager.addTemplate(lobby);
                    templateManager.reloadTemplates();

                    PoloLogger.print(LogLevel.ERROR, "§7Created §b" + proxy.getName() + " §7as §eProxy-Template §7and §3" + lobby.getName() + " §7as §6Lobby-Template§7!");
                    PoloLogger.print(LogLevel.INFO, "You " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "the setup.");
                } else {

                    //Proxy setup
                    PoloLogger.print(LogLevel.INFO, "Entering setup for §bProxy-Group§7...");
                    new TemplateSetup(PoloCloudAPI.getInstance().getTemplateManager()).sendSetup();

                    //Lobby setup
                    PoloLogger.print(LogLevel.INFO, "Entering setup for §bLobby-Group§7...");
                    new TemplateSetup(PoloCloudAPI.getInstance().getTemplateManager()).sendSetup();
                }

                ConsoleRunner.getInstance().setActive(true);
                masterConfig.setProperties(properties);
                masterConfig.update();
                PoloCloudAPI.getInstance().getConfigSaver().save(PoloCloudClient.getInstance().getClientConfig(), new File("client.json"));
            }
        });
        setupBuilder.nextQuestion(step, ConsoleRunner.getInstance().getConsoleReader());
    }

    @Override
    public void cancelSetup() {
        PoloLogger.print(LogLevel.ERROR, "§cYou are not allowed to cancel the §eMasterSetup§c!");
        System.exit(-1);
    }
}
