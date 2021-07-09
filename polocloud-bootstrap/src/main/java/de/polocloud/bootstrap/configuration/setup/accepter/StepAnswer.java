package de.polocloud.bootstrap.configuration.setup.accepter;

import de.polocloud.bootstrap.configuration.setup.Step;

import java.util.List;

public abstract class StepAnswer {

    public abstract void callFinishSetup(List<Step> steps);

}
