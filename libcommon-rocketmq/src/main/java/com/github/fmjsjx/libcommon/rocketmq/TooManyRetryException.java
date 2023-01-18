package com.github.fmjsjx.libcommon.rocketmq;

import java.io.Serial;
import java.util.Collection;

/**
 * Exception for too many retry.
 */
public class TooManyRetryException extends RocketMQException {

    @Serial
    private static final long serialVersionUID = 7143769630958281001L;

    private final Iterable<Throwable> causes;
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
