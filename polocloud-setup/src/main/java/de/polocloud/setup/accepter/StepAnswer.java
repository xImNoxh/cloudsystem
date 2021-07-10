package de.polocloud.setup.accepter;

import de.polocloud.setup.Step;

import java.util.List;

public abstract class StepAnswer {

    public abstract void callFinishSetup(List<Step> steps);

}
