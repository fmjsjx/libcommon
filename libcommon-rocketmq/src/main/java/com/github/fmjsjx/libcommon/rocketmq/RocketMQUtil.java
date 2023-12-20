package com.github.fmjsjx.libcommon.rocketmq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for RocketMQ.
 */
public class RocketMQUtil {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQUtil.class);

    private static final int defaultRetryTimes = 3;

    /**
     * Uses the specified producer to send the specified message.
     * 
     * @param producer the producer
     * @param msg      the message
     * @return a {@code SendResult}
     * @throws MQClientException     if any {@link MQClientException} occurs
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(MQProducer producer, Message msg)
            throws MQClientException, TooManyRetryException {
        return send(producer, msg, defaultRetryTimes);
    }

    /**
     * Uses the specified producer to send the specified message.
     * 
     * @param producer   the producer
     * @param msg        the message
     * @param retryTimes times for retry
     * @return a {@code SendResult}
     * @throws MQClientException     if any {@link MQClientException} occurs
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(MQProducer producer, Message msg, int retryTimes)
            throws MQClientException, TooManyRetryException {
        try {
            return producer.send(msg);
        } catch (Exception e) {
            if (e instanceof MQClientException ex) {
                throw ex;
            }
            logger.warn("Send message failed, start retry stage: {}", msg, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(msg);
                } catch (Exception cause) {
                    if (e instanceof MQClientException ex) {
                        throw ex;
                    }
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send message failed", e, causes);
        }
    }

    /**
     * Uses the specified producer to send the specified message asynchronously.
     * 
     * @param producer the producer
     * @param msg      the message
     * @return a {@code CompletableFuture<SendResult>}
     */
    public static final CompletableFuture<SendResult> sendAsync(MQProducer producer, Message msg) {
        return sendAsync(producer, msg, defaultRetryTimes);
    }

    /**
     * Uses the specified producer to send the specified message asynchronously.
     * 
     * @param producer   the producer
     * @param msg        the message
     * @param retryTimes times for retry
     * @return a {@code CompletableFuture<SendResult>}
     */
    public static final CompletableFuture<SendResult> sendAsync(MQProducer producer, Message msg, int retryTimes) {
        var future = new SendFuture(producer, msg, retryTimes);
        try {
            producer.send(msg, future);
            return future;
        } catch (Exception e) {
            future.onException(e);
            return future;
        }
    }

    private static final class SendFuture extends CompletableFuture<SendResult> implements SendCallback {

        private final MQProducer producer;
        private final Message msg;
        private final AtomicInteger retryTimes;
        private final AtomicReference<Throwable> causeRef = new AtomicReference<>();
        private final AtomicReference<List<Throwable>> causesRef = new AtomicReference<>();

        public SendFuture(MQProducer producer, Message msg, int retryTimes) {
            this.producer = producer;
            this.msg = msg;
            this.retryTimes = new AtomicInteger(retryTimes);
        }

        @Override
        public void onSuccess(SendResult sendResult) {
            complete(sendResult);
        }

        @Override
        public void onException(Throwable e) {
            if (e instanceof MQClientException) {
                completeExceptionally(e);
                return;
            }
            if (causeRef.compareAndSet(null, e)) {
                causesRef.set(new ArrayList<>());
            } else {
                causesRef.get().add(e);
            }
            var remaining = retryTimes.getAndDecrement();
            if (remaining > 0) {
                try {
                    producer.send(msg, this);
                } catch (Exception cause) {
                    onException(cause);
                }
            } else {
                completeExceptionally(
                        new TooManyRetryException("send message async failed", causeRef.get(), causesRef.get()));
            }
        }

    }

    private RocketMQUtil() {
    }
}
