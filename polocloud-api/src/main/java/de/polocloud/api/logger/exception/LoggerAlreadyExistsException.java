package de.polocloud.api.logger.exception;

public class LoggerAlreadyExistsException extends RuntimeException {

    public LoggerAlreadyExistsException(String message) {
        super(message);
    }
}
