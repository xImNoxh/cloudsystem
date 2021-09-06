package de.polocloud.modules.permission.global.setup;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.api.setup.FutureAnswer;
import de.polocloud.api.setup.Setup;
import de.polocloud.api.setup.SetupBuilder;
import de.polocloud.api.setup.Step;
import de.polocloud.api.setup.accepter.StepAcceptor;
import de.polocloud.api.setup.accepter.StepAnswer;
import de.polocloud.modules.permission.global.api.IPermissionGroup;
import de.polocloud.modules.permission.global.api.PermissionPool;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionDisplay;
import de.polocloud.modules.permission.global.api.impl.SimplePermissionGroup;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroupSetup extends StepAcceptor implements Setup {

    @Override
    public void sendSetup() {
        SetupBuilder setupBuilder = new SetupBuilder(this);
        Step step = setupBuilder.createStep("What is the name of the new PermissionGroup?");

        step.addStep("Whats the Id of this PermissionGroup (Priority) ?", isInteger())
            .addStep("What color should this PermissionGroup have?", new FutureAnswer() {

                @Override
                public Object[] findPossibleAnswers(SetupBuilder steps) {
                    return MinecraftColor.values();
                }
            })
            .addStep("What prefix should this PermissionGroup have?")
            .addStep("What suffix should this PermissionGroup have?")
            .addStep("What chatFormat should this PermissionGroup have?");


        setupBuilder.setStepAnswer(new StepAnswer() {
            @Override
            public void callFinishSetup(List<Step> steps) {
                String name = steps.get(0).getAnswer();
                int id = steps.get(1).getAnswerAsInt();
                MinecraftColor color = MinecraftColor.valueOf(steps.get(2).getAnswer());
                String prefix = steps.get(3).getAnswer();
                String suffix = steps.get(4).getAnswer();
                String chatFormat = steps.get(5).getAnswer();

                IPermissionGroup group = new SimplePermissionGroup(name, id, false, new ArrayList<>(), new SimplePermissionDisplay(color, prefix, suffix, chatFormat), new ArrayList<>());

                PermissionPool.getInstance().createPermissionGroup(group);
                PoloLogger.print(LogLevel.INFO, "§7You §asuccessfully §7created the group " + color.getColor() + group.getName() + "§7!");
            }
        });
        setupBuilder.nextQuestion(step, PoloCloudAPI.getInstance().getConsoleReader());
    }

    @Override
    public void cancelSetup() {
        PoloLogger.print(LogLevel.INFO, "§7You §asuccessfully §7cancelled the setup§7!");
    }
}
