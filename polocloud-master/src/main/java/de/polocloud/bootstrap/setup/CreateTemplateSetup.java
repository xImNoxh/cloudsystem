package de.polocloud.bootstrap.setup;

import de.polocloud.api.template.GameServerVersion;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.template.SimpleTemplate;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.setup.FutureAnswer;
import de.polocloud.setup.Setup;
import de.polocloud.setup.SetupBuilder;
import de.polocloud.setup.Step;
import de.polocloud.setup.accepter.StepAcceptor;
import de.polocloud.setup.accepter.StepAnswer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CreateTemplateSetup extends StepAcceptor implements Setup {

    private ITemplateService templateService;

    public CreateTemplateSetup(ITemplateService templateService) {
        this.templateService = templateService;
    }

    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("What is the name of the new template?");

        step.addStep("What is the minimum amount of services?", isInteger())
            .addStep("What is the maximal amount of services?", isInteger())
            .addStep("What is the amount of max players?", isInteger())
            .addStep("What is the maximal memory of this service", isInteger())
            .addStep("What is the template type of this service?", TemplateType.MINECRAFT.getDisplayName(), TemplateType.PROXY.getDisplayName())
            .addStep("What is the game version?", new FutureAnswer() {
                @Override
                public Object[] findPossibleAnswers(SetupBuilder steps) {
                    TemplateType templateType = TemplateType.valueOf(setupBuilder.getAnswers().get(5).getAnswer().toUpperCase());
                    return GameServerVersion.prettyValues(templateType);
                }
            })
            .addStep("What is name of the Wrapper(s) ?")
            .addStep("Static ? (true/false)", isBoolean())
            .addStep("ServerCreateThreshold ? (0-100)%", isInteger());

        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                String name = steps.get(0).getAnswer();
                int maxServerCount = Integer.parseInt(steps.get(2).getAnswer());
                int minServerCount = Integer.parseInt(steps.get(1).getAnswer());
                int maxPlayers = Integer.parseInt(steps.get(3).getAnswer());
                int memory = Integer.parseInt(steps.get(4).getAnswer());

                TemplateType templateType = TemplateType.valueOf(steps.get(5).getAnswer().toUpperCase());
                GameServerVersion gameServerVersion = Arrays.stream(GameServerVersion.values()).filter(key -> key.getTitle().equalsIgnoreCase(steps.get(6).getAnswer())).findAny().get();
                String[] wrappers = steps.get(7).getAnswer().replaceAll(" ", "").split(",");

                boolean isStatic = Boolean.parseBoolean(steps.get(8).getAnswer());
                int threshold = Integer.parseInt(steps.get(9).getAnswer());

                ITemplate template = new SimpleTemplate(name, isStatic, maxServerCount, minServerCount, templateType, gameServerVersion, maxPlayers, memory, true, "A default Polo Service", threshold, wrappers);
                templateService.getTemplateSaver().save(template);
                templateService.reloadTemplates();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "You " + ConsoleColors.GREEN + "complete " + ConsoleColors.GRAY + "the setup.");
            }
        });
        setupBuilder.nextQuestion(step, Logger.getConsoleReader());
    }

    @Override
    public void cancelSetup() {

    }
}
