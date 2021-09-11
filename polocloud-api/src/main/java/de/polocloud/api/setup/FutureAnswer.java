package de.polocloud.api.setup;

public abstract class FutureAnswer {

    public abstract Object[] findPossibleAnswers(SetupBuilder steps);

    public String formatAnswer(Object arg) {
        return null;
    }

}
