package de.polocloud.api.setup;

import lombok.Setter;

import java.util.function.Predicate;

@Setter
public class Step {

    private final String question;
    private Predicate<String> acceptor;
    private String[] defaultAnswers;
    private FutureAnswer futureAnswer;

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

    public Step(String question, Predicate<String> acceptor, FutureAnswer futureAnswer) {
        this.question = question;
        this.acceptor = acceptor;
        this.futureAnswer = futureAnswer;
    }

    public Step addStep(String question, Predicate<String> acceptor, String... possibleAnswers) {
        Step step = new Step(question, acceptor, possibleAnswers);
        nextStep = step;
        return step;
    }

    public Step addStep(String question, Object... possibleAnswers) {
        String[] s = new String[possibleAnswers.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = possibleAnswers[i].toString();
        }
        return addStep(question, s);
    }

    public Step addStep(String question, String... possibleAnswers) {
        Step step = new Step(question, o -> true, possibleAnswers);
        nextStep = step;
        return step;
    }

    public Step addStep(String question, FutureAnswer futureAnswer) {
        Step step = new Step(question, o -> true, futureAnswer);
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

    public FutureAnswer getFutureAnswer() {
        return futureAnswer;
    }

    public int getAnswerAsInt() {
        return Integer.parseInt(getAnswer());
    }

    public boolean getAnswerAsBoolean() {
        return Boolean.parseBoolean(getAnswer());
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
