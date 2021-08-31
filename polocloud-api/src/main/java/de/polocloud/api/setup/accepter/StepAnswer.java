package de.polocloud.api.setup.accepter;

import de.polocloud.api.setup.Step;

import java.util.List;

public abstract class StepAnswer {

    public abstract void callFinishSetup(List<Step> steps);

}
