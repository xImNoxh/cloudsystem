package de.polocloud.wrapper.setup;

import de.polocloud.api.PoloCloudAPI;
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
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.impl.config.WrapperConfig;

import java.util.List;

public class WrapperSetup extends StepAcceptor implements Setup {


    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);

        Step step = setupBuilder.createStep("On what port does the PoloCloud-Master run? (Port number)", isInteger());

        step.addStep("On What Host does your PoloCloud-Master run? (Ip-Address)")
            .addStep("What should this Wrapper be called?")
            .addStep("Please enter the WrapperKey from the config.json of the PoloCloud-Master!")
            .addStep("Should Server-Output (Screens) be logged and saved as files? (true / false)", isBoolean());

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                int port = steps.get(0).getAnswerAsInt();
                String host = steps.get(1).getAnswer();
                String name = steps.get(2).getAnswer();
                String key = steps.get(3).getAnswer();
                boolean logServerOutput = steps.get(4).getAnswerAsBoolean();


                WrapperConfig config = Wrapper.getInstance().getConfig();

                config.setLoginKey(key);
                config.setWrapperName(name);
                config.setLogServerOutput(logServerOutput);
                config.setMasterAddress(host + ":" + port);
                PoloCloudAPI.getInstance().getConfigSaver().save(config, FileConstants.WRAPPER_CONFIG_FILE);

                PoloLogger.print(LogLevel.INFO, "You " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "the setup.");
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
