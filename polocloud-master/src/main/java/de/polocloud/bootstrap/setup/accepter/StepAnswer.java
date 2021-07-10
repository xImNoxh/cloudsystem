package de.polocloud.bootstrap.setup.accepter;

import de.polocloud.bootstrap.setup.Step;

import java.util.List;

public abstract class StepAnswer {

    public abstract void callFinishSetup(List<Step> steps);

}
