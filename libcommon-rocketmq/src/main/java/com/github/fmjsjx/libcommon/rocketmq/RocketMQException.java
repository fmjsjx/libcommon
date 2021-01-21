package com.github.fmjsjx.libcommon.rocketmq;

/**
 * Base exception for RocketMQ.
 */
public class RocketMQException extends Exception {

    private static final long serialVersionUID = -7361335888820087842L;

    /**
     * Constructs a new {@link RocketMQException} with {@code null} as its detail
     * message. The cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     */
    public RocketMQException() {
        super();
    }

    /**
     * Constructs a new {@link RocketMQException} with the specified detail message
     * and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this exception's detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). (A {@code null} value is
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     */
    public RocketMQException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@link RocketMQException} with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call
     * to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later
     *                retrieval by the {@link #getMessage()} method.
     */
    public RocketMQException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link RocketMQException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}). This
     * constructor is useful for exceptions that are little more than wrappers for
     * other {@link Throwable}s (for example,
     * {@link java.security.PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *              {@link #getCause()} method). (A {@code null} value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     */
    public RocketMQException(Throwable cause) {
        super(cause);
    }

}
