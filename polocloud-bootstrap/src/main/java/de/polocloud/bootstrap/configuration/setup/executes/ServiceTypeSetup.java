package de.polocloud.bootstrap.configuration.setup.executes;

import de.polocloud.bootstrap.configuration.setup.Setup;
import de.polocloud.bootstrap.configuration.setup.SetupBuilder;
import de.polocloud.bootstrap.configuration.setup.Step;
import de.polocloud.bootstrap.configuration.setup.accepter.StepAnswer;
import de.polocloud.logger.log.Logger;

import java.util.List;

public class ServiceTypeSetup implements Setup {


    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("Master or Wrapper?", "Master", "Wrapper");

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {

            }
        });
        setupBuilder.nextQuestion(step, Logger.getConsoleReader());
    }

    @Override
    public void cancelSetup() {

    }
}
