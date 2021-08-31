package de.polocloud.api.setup.accepter;

import java.util.function.Predicate;

public abstract class StepAcceptor {

    public Predicate<String> isInteger() {
        return s -> {
            try {
                Integer.parseInt(String.valueOf(s));
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        };
    }

    public Predicate<String> isBoolean() {
        return s -> s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

}
