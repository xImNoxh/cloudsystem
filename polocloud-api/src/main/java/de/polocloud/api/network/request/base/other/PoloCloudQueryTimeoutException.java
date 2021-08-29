package de.polocloud.api.network.request.base.other;


import de.polocloud.api.util.PoloHelper;

public class PoloCloudQueryTimeoutException extends RuntimeException {

    private static final long serialVersionUID = -9111660455596144261L;

    /**
     * The extra message of this error
     */
    private final String message;

    /**
     * The code of this error
     */
    private final int code;

    /**
     * The parent of this exception
     */
    private final String parentClass;

    public PoloCloudQueryTimeoutException(Throwable throwable) {
        this.parentClass = throwable.getClass().getName();
        this.message = throwable.getMessage();
        this.code = 0x00;
    }

    public PoloCloudQueryTimeoutException(String message) {
        super(message);
        this.message = message;
        this.code = 0x00;
        this.parentClass = PoloCloudQueryTimeoutException.class.getName();
    }

    public PoloCloudQueryTimeoutException(String message, int code, Class<? extends Exception> parentClass) {
        super(message);
        this.message = message;
        this.code = code;
        this.parentClass = parentClass.getName();
    }

    public Class<? extends Exception> getParentClass() {
        return PoloHelper.sneakyThrows(() -> (Class<? extends Exception>) Class.forName(parentClass));
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
