package de.polocloud.setup;

import java.util.function.Predicate;

public class Step {

    private final String question;
    private final Predicate<String> acceptor;
    private final String[] defaultAnswers;
    private String answer;
    private Step nextStep;

    public Step(String question, Predicate<String> acceptor, String[] defaultAnswers) {
        this.question = question;
        this.acceptor = acceptor;
        this.defaultAnswers = defaultAnswers;
    }

    public Step(String question, String[] defaultAnswers) {
        this.question = question;
        this.acceptor = o -> true;
        this.defaultAnswers = defaultAnswers;
    }


    public Step addStep(String question, Predicate<String> acceptor, String... possibleAnswers){
        Step step = new Step(question, acceptor, possibleAnswers);
        nextStep = step;
        return step;
    }

    public Step addStep(String question, String... possibleAnswers){
        Step step = new Step(question, o -> true, possibleAnswers);
        nextStep = step;
        return step;
    }

    public Predicate<String> getAcceptor() {
        return acceptor;
    }

    public Step getNextStep() {
        return nextStep;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getDefaultAnswers() {
        return defaultAnswers;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

}
