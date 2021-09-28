package de.polocloud.api.wrapper.ex;

public class NoWrapperFoundException extends RuntimeException {

    public NoWrapperFoundException() {
        super();
    }

    public NoWrapperFoundException(String s) {
        super(s);
    }
}
