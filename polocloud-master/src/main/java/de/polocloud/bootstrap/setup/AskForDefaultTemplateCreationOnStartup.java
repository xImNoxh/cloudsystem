package de.polocloud.bootstrap.setup;

import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.template.SimpleTemplate;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.setup.Setup;
import de.polocloud.setup.SetupBuilder;
import de.polocloud.setup.Step;
import de.polocloud.setup.accepter.StepAcceptor;
import de.polocloud.setup.accepter.StepAnswer;

import java.util.List;

public class AskForDefaultTemplateCreationOnStartup extends StepAcceptor implements Setup {

    private ITemplateService templateService;

    public AskForDefaultTemplateCreationOnStartup(ITemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("It seems you don't have any templates... Should the cloud enable fast start?", isBoolean());

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                boolean enabled = Boolean.parseBoolean(steps.get(0).getAnswer());
                if (enabled) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Creating please wait...");
                    ITemplate template = new SimpleTemplate("Proxy", false, 1, 1, TemplateType.PROXY, GameServerVersion.PROXY, 50, 512, true, "A default Polo Service", 100, new String[]{"Wrapper-1"});
                    templateService.getTemplateSaver().save(template);
                    templateService.reloadTemplates();
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Created proxy template.");
                    template = new SimpleTemplate("Lobby", false, 2, 1, TemplateType.MINECRAFT, GameServerVersion.PAPERSPIGOT_1_8_8, 50, 512, true, "A default Polo Service", 100, new String[]{"Wrapper-1"});
                    templateService.getTemplateSaver().save(template);
                    templateService.reloadTemplates();
                    Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully " + ConsoleColors.GRAY + "finished fast start!");
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Starting servers if a wrapper is online...");
                } else {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "You have declined fast start.");
                }
            }
        });
        setupBuilder.nextQuestion(step, Logger.getConsoleReader());
    }


    @Override
    public void cancelSetup() {

    }
}
