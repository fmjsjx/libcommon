package com.github.fmjsjx.libcommon.aliyunons;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;

/**
 * Utility class for ALIYUN ONS.
 */
public class AliyunOnsUtil {

    private static final Logger logger = LoggerFactory.getLogger(AliyunOnsUtil.class);

    private static final int defaultRetryTimes = 3;

    /**
     * Uses the specified producer to send the specified message.
     * 
     * @param producer the producer
     * @param message  the message
     * @return a {@code SendResult}
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(Producer producer, Message message) throws TooManyRetryException {
        return send(producer, message, defaultRetryTimes);
    }

    /**
     * Uses the specified producer to send the specified message.
     * 
     * @param producer   the producer
     * @param message    the message
     * @param retryTimes times for retry
     * @return a {@code SendResult}
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(Producer producer, Message message, int retryTimes)
            throws TooManyRetryException {
        try {
            return producer.send(message);
        } catch (Throwable e) {
            logger.warn("Send message failed, start retry stage: {}", message, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(message);
                } catch (Throwable cause) {
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send message failed", e, causes);
        }
    }

    /**
     * Uses the specified order producer to send the specified message with
     * specified shardingKey.
     * 
     * @param producer    the producer
     * @param message     the message
     * @param shardingKey the sharding key
     * @return a {@code SendResult}
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(OrderProducer producer, Message message, String shardingKey)
            throws TooManyRetryException {
        return send(producer, message, shardingKey, defaultRetryTimes);
    }

    /**
     * Uses the specified order producer to send the specified message with
     * specified shardingKey.
     * 
     * @param producer    the producer
     * @param message     the message
     * @param shardingKey the sharding key
     * @param retryTimes  times for retry
     * @return a {@code SendResult}
     * @throws TooManyRetryException if too many retry occurs
     */
    public static final SendResult send(OrderProducer producer, Message message, String shardingKey, int retryTimes)
            throws TooManyRetryException {
        try {
            return producer.send(message, shardingKey);
        } catch (Throwable e) {
            logger.warn("Send order message failed, start retry stage: {}", message, e);
            // retry stage
            List<Throwable> causes = new ArrayList<>();
            for (var remainingTimes = Math.max(0, retryTimes); remainingTimes > 0; remainingTimes--) {
                try {
                    return producer.send(message, shardingKey);
                } catch (Throwable cause) {
                    causes.add(cause);
                }
            }
            throw new TooManyRetryException("send order message failed", e, causes);
        }
    }

    /**
     * Uses the specified producer to send the specified message asynchronously.
     * 
     * @param producer the producer
     * @param message  the message
     * @return a {@code CompletableFuture<SendResult>}
     */
    public static final CompletableFuture<SendResult> sendAsync(Producer producer, Message message) {
        return sendAsync(producer, message, defaultRetryTimes);
    }

    /**
     * Uses the specified producer to send the specified message asynchronously.
     * 
     * @param producer   the producer
     * @param message    the message
     * @param retryTimes times for retry
     * @return a {@code CompletableFuture<SendResult>}
     */
    public static final CompletableFuture<SendResult> sendAsync(Producer producer, Message message, int retryTimes) {
        var future = new SendFuture(producer, message, retryTimes);
        producer.sendAsync(message, future);
        return future;
    }

    private static final class SendFuture extends CompletableFuture<SendResult> implements SendCallback {

        private final Producer producer;
        private final Message message;
        private final AtomicInteger retryTimes;
        private final AtomicReference<Throwable> causeRef = new AtomicReference<>();
        private final AtomicReference<List<Throwable>> causesRef = new AtomicReference<>();

        public SendFuture(Producer producer, Message message, int retryTimes) {
            this.producer = producer;
            this.message = message;
            this.retryTimes = new AtomicInteger(retryTimes);
        }

        @Override
        public void onSuccess(SendResult sendResult) {
            complete(sendResult);
        }

        @Override
        public void onException(OnExceptionContext context) {
            var e = context.getException();
            if (causeRef.compareAndSet(null, e)) {
                causesRef.set(new ArrayList<>());
            } else {
                causesRef.get().add(e);
            }
            var remaining = retryTimes.getAndDecrement();
            if (remaining > 0) {
                producer.sendAsync(message, this);
            } else {
                completeExceptionally(
                        new TooManyRetryException("send message async failed", causeRef.get(), causesRef.get()));
            }
        }

    }

    private AliyunOnsUtil() {
    }
}
