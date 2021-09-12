package de.polocloud.wrapper.setup;

import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.setup.Setup;
import de.polocloud.api.setup.SetupBuilder;
import de.polocloud.api.setup.Step;
import de.polocloud.api.setup.accepter.StepAcceptor;
import de.polocloud.api.setup.accepter.StepAnswer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;

import java.util.List;

public class WrapperSetup extends StepAcceptor implements Setup {


    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);

        Step step = setupBuilder.createStep("On what port does the Master run?", isInteger());

        step.addStep("On What Host does the Master run?")
            .addStep("What should this Wrapper be called?")
            .addStep("Please enter the WrapperKey from the config.json of the Master!")
            .addStep("Should Server-Output (Screens) be logged and saved as files?", isBoolean());

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                int port = steps.get(0).getAnswerAsInt();
                String host = steps.get(1).getAnswer();
                String name = steps.get(2).getAnswer();
                String key = steps.get(3).getAnswer();
                boolean logServerOutput = steps.get(4).getAnswerAsBoolean();

                PoloLogger.print(LogLevel.INFO, "You " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "the setup.");
            }
        });


        setupBuilder.nextQuestion(step, Logger.getConsoleReader());
    }

    @Override
    public void cancelSetup() {
        PoloLogger.print(LogLevel.ERROR, "§cYou are not allowed to cancel the §eWrapperSetup§c!");
        System.exit(-1);
    }
}
