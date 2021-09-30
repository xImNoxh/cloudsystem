package de.polocloud.wrapper.setup;

import de.polocloud.api.config.FileConstants;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.setup.Setup;
import de.polocloud.api.setup.SetupBuilder;
import de.polocloud.api.setup.Step;
import de.polocloud.api.setup.accepter.StepAcceptor;
import de.polocloud.api.setup.accepter.StepAnswer;
import de.polocloud.api.console.ConsoleRunner;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.WrapperConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class WrapperSetup extends StepAcceptor implements Setup {

    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);

        Step step = setupBuilder.createStep("On what port does the PoloCloud-Master run? (Port number)", isInteger());

        step.addStep("On What Host does your PoloCloud-Master run? (Ip-Address)")
            .addStep("What should this Wrapper be called?")
            .addStep("How much memory is this Wrapper allowed to use (In MB)?")
            .addStep("Should Server-Output (Screens) be logged and saved as files? (true / false)", isBoolean())
            .addStep("Do you want to download every Minecraft-Server-Software (Proxy and Spigot) to avoid loading times when starting a new GameServer? (Note: this might take some time if your internet connection is slow)", isBoolean())
            .addStep("Please enter the WrapperKey from the config.json of the PoloCloud-Master!");

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                int port = steps.get(0).getAnswerAsInt();
                String host = steps.get(1).getAnswer();
                String name = steps.get(2).getAnswer();
                int memory = steps.get(3).getAnswerAsInt();
                boolean logServerOutput = steps.get(4).getAnswerAsBoolean();
                boolean download = steps.get(5).getAnswerAsBoolean();
                String key = steps.get(6).getAnswer();


                WrapperConfig config = Wrapper.getInstance().getConfig();

                config.setMemory(memory);
                config.setLoginKey(key);
                config.setWrapperName(name);
                config.setLogServerOutput(logServerOutput);
                config.setMasterAddress(host + ":" + port);
                config.save(FileConstants.WRAPPER_CONFIG_FILE);

                if (download) {
                    long start = 0;
                    PoloLogger.print(LogLevel.INFO, "§7You requested to §adownload §7every §bGameServerSoftware§7! This might take some time...");
                    for (GameServerVersion version : GameServerVersion.values()) {
                        PoloLogger.print(LogLevel.INFO, "§7§7...");
                        start = System.currentTimeMillis();
                        PoloLogger.print(LogLevel.INFO, "§7Now downloading §b" + version.getTitle() + "§7...");
                        try {
                            File versionFile = new File(FileConstants.WRAPPER_STORAGE_VERSIONS, version.getTitle() + ".jar");
                            if (versionFile.exists()) {
                                PoloLogger.print(LogLevel.INFO, "§7Skipping §6" + version.getTitle() + " §7because it already exists§7!");
                                continue;
                            }
                            FileUtils.copyURLToFile(new URL(version.getUrl()), versionFile);
                            PoloLogger.print(LogLevel.INFO, "§7Downloaded §b" + version.getTitle() + "§7 in §a" + (System.currentTimeMillis() - start) + "ms§7!");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    PoloLogger.print(LogLevel.INFO, "§7Downloaded every §bGameServerSoftware §7within §e" + (System.currentTimeMillis() - start) + "ms§7!");
                }

                PoloLogger.print(LogLevel.INFO, "You " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "the setup.");
                PoloLogger.print(LogLevel.INFO, "§cThe Wrapper will now §eshutdown§c! You have to restart to apply all changes and confirm that no bugs appear!");
                System.exit(0);

            }
        });

        setupBuilder.nextQuestion(step, ConsoleRunner.getInstance().getConsoleReader());
    }

    @Override
    public void cancelSetup() {
        PoloLogger.print(LogLevel.ERROR, "§cYou are not allowed to cancel the §eWrapperSetup§c!");
        System.exit(-1);
    }
}
