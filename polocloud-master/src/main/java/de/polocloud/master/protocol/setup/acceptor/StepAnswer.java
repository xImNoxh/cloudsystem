package de.polocloud.master.protocol.setup.acceptor;

import de.polocloud.master.protocol.setup.Step;

import java.util.List;

public abstract class StepAnswer {

    public abstract void callFinishSetup(List<Step> steps);

}
