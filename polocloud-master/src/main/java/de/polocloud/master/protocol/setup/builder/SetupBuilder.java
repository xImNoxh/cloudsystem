package de.polocloud.master.protocol.setup.builder;

import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.master.protocol.setup.Setup;
import de.polocloud.master.protocol.setup.Step;
import de.polocloud.master.protocol.setup.acceptor.StepAnswer;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SetupBuilder {

    private final Setup setup;
    private Step firstStep;
    private StepAnswer stepAnswer;
    private final List<Step> answers = new ArrayList<>();

    public SetupBuilder(Setup setup) {
        this.setup = setup;
    }

    public Step createStep(String question, Predicate<String> acceptor, String... possibleAnswers) {
        firstStep = new Step(question, acceptor, possibleAnswers);
        return this.firstStep;
    }

    public Step createStep(String question, String... possibleAnswers) {
        firstStep = new Step(question, possibleAnswers);
        return this.firstStep;
    }

    public void nextQuestion(Step currentStep, ConsoleReader consoleReader) {
        if (!hasNextStep(currentStep)) {
            stepAnswer.callFinishSetup(answers);
            return;
        }
        sendQuestion(currentStep);
        String answer = null;
        try {
            answer = consoleReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (answer.equalsIgnoreCase("cancel")) {
            setup.cancelSetup();
            return;
        }

        if (!isAnswerAccepted(currentStep, answer) || isPossibleAnswer(currentStep, answer)) {
            nextQuestion(currentStep, consoleReader);
            return;
        }

        currentStep.setAnswer(answer);
        this.answers.add(currentStep);
        nextQuestion(currentStep.getNextStep(), consoleReader);
    }

    public void sendQuestion(Step step) {
        Logger.log(LoggerType.INFO, step.getQuestion());
        if (step.getDefaultAnswers().length > 0) {
            Logger.log(LoggerType.INFO, "[" + getAnswerKeys(step) + "]");
        }
    }

    public String getAnswerKeys(Step step) {
        return String.join(",", step.getDefaultAnswers());
    }

    public boolean hasNextStep(Step step) {
        return step != null;
    }

    public boolean isAnswerAccepted(Step step, String answer) {
        return step.getAcceptor().test(answer);
    }

    public void setStepAnswer(StepAnswer stepAnswer) {
        this.stepAnswer = stepAnswer;
    }

    public boolean isPossibleAnswer(Step step, String value) {
        return step.getDefaultAnswers().length > 0 && Arrays.stream(step.getDefaultAnswers()).noneMatch(type -> type.equalsIgnoreCase(value));
    }


}
