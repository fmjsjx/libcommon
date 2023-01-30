package com.github.fmjsjx.libcommon.aliyunons;

import java.io.Serial;
import java.util.Collection;

/**
 * Exception for too many retry.
 */
public class TooManyRetryException extends ONSException {

    @Serial
    private static final long serialVersionUID = 2093368191526503232L;

    /**
     * The history causes.
     */
    private final Iterable<Throwable> causes;
    /**
     * The retry times.
     */
    private final int retryTimes;

    /**
     * Constructs a new {@link TooManyRetryException} with the specified detail
     * message, cause and causes.
     * 
     * @param message the detail message
     * @param cause   the cause
     * @param causes  the history causes
     */
    public TooManyRetryException(String message, Throwable cause, Collection<Throwable> causes) {
        super(message, cause);
        this.causes = causes;
        this.retryTimes = causes.size();
    }

    /**
     * Constructs a new {@link TooManyRetryException} with the specified cause and
     * causes.
     * 
     * @param cause  the cause
     * @param causes the history causes
     */
    public TooManyRetryException(Throwable cause, Collection<Throwable> causes) {
        super(cause);
        this.causes = causes;
        this.retryTimes = causes.size();
    }

    /**
     * Returns the history causes.
     * 
     * @return the history causes
     */
    public Iterable<Throwable> getCauses() {
        return causes;
    }

    /**
     * Returns the retry times.
     * 
     * @return the retry times
     */
    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public String getLocalizedMessage() {
        var msg = getMessage();
        return msg == null ? "retry times " + retryTimes : msg + ", retry times " + retryTimes;
    }

}
