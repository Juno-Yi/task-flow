package com.junoyi.framework.redis.utils;

import com.junoyi.framework.core.utils.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 队列工具类，提供基于Redisson的各种队列操作封装。
 * 包括普通阻塞队列、延迟队列、优先级队列以及有界队列的操作方法。
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueueUtils {

    /**
     * Redisson客户端实例，用于执行各种Redisson队列操作。
     */
    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 获取Redisson客户端实例。
     *
     * @return Redisson客户端实例
     */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    /**
     * 向指定名称的阻塞队列中添加元素。
     *
     * @param queueName 队列名称
     * @param data      要添加的数据对象
     * @param <T>       数据类型泛型
     * @return 添加成功返回true，否则返回false
     */
    public static <T> boolean addQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.offer(data);
    }

    /**
     * 从指定名称的阻塞队列中取出一个元素（非阻塞）。
     *
     * @param queueName 队列名称
     * @param <T>       数据类型泛型
     * @return 取出的元素，如果队列为空则返回null
     */
    public static <T> T getQueueObject(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.poll();
    }

    /**
     * 从指定名称的阻塞队列中移除指定元素。
     *
     * @param queueName 队列名称
     * @param data      要移除的数据对象
     * @param <T>       数据类型泛型
     * @return 移除成功返回true，否则返回false
     */
    public static <T> boolean removeQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.remove(data);
    }

    /**
     * 删除指定名称的阻塞队列。
     *
     * @param queueName 队列名称
     * @param <T>       数据类型泛型
     * @return 删除成功返回true，否则返回false
     */
    public static <T> boolean destroyQueue(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.delete();
    }

    /**
     * 向指定名称的延迟队列中添加元素，默认时间单位为毫秒。
     *
     * @param queueName 队列名称
     * @param data      要添加的数据对象
     * @param time      延迟时间（毫秒）
     * @param <T>       数据类型泛型
     */
    public static <T> void addDelayedQueueObject(String queueName, T data, long time) {
        addDelayedQueueObject(queueName, data, time, TimeUnit.MILLISECONDS);
    }

    /**
     * 向指定名称的延迟队列中添加元素，并指定时间单位。
     *
     * @param queueName 队列名称
     * @param data      要添加的数据对象
     * @param time      延迟时间
     * @param timeUnit  时间单位
     * @param <T>       数据类型泛型
     */
    public static <T> void addDelayedQueueObject(String queueName, T data, long time, TimeUnit timeUnit) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        delayedQueue.offer(data, time, timeUnit);
    }

    /**
     * 从指定名称的延迟队列中取出一个元素（非阻塞）。
     *
     * @param queueName 队列名称
     * @param <T>       数据类型泛型
     * @return 取出的元素，如果队列为空则返回null
     */
    public static <T> T getDelayedQueueObject(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        return delayedQueue.poll();
    }

    /**
     * 从指定名称的延迟队列中移除指定元素。
     *
     * @param queueName 队列名称
     * @param data      要移除的数据对象
     * @param <T>       数据类型泛型
     * @return 移除成功返回true，否则返回false
     */
    public static <T> boolean removeDelayedQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        return delayedQueue.remove(data);
    }

    /**
     * 销毁指定名称的延迟队列。
     *
     * @param queueName 队列名称
     * @param <T>       数据类型泛型
     */
    public static <T> void destroyDelayedQueue(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        delayedQueue.destroy();
    }

    /**
     * 向指定名称的优先级阻塞队列中添加元素。
     *
     * @param queueName 队列名称
     * @param data      要添加的数据对象
     * @param <T>       数据类型泛型
     * @return 添加成功返回true，否则返回false
     */
    public static <T> boolean addPriorityQueueObject(String queueName, T data) {
        RPriorityBlockingQueue<T> priorityBlockingQueue = CLIENT.getPriorityBlockingQueue(queueName);
        return priorityBlockingQueue.offer(data);
    }

    /**
     * 尝试设置指定名称的有界阻塞队列容量。
     *
     * @param queueName 队列名称
     * @param capacity  容量大小
     * @param <T>       数据类型泛型
     * @return 设置成功返回true，否则返回false
     */
    public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        return boundedBlockingQueue.trySetCapacity(capacity);
    }

    /**
     * 尝试设置指定名称的有界阻塞队列容量，可选择是否在已存在时销毁原队列。
     *
     * @param queueName 队列名称
     * @param capacity  容量大小
     * @param destroy   是否销毁已有队列
     * @param <T>       数据类型泛型
     * @return 设置成功返回true，否则返回false
     */
    public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity, boolean destroy) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        if (boundedBlockingQueue.isExists() && destroy) {
            destroyQueue(queueName);
        }
        return boundedBlockingQueue.trySetCapacity(capacity);
    }

    /**
     * 向指定名称的有界阻塞队列中添加元素。
     *
     * @param queueName 队列名称
     * @param data      要添加的数据对象
     * @param <T>       数据类型泛型
     * @return 添加成功返回true，否则返回false
     */
    public static <T> boolean addBoundedQueueObject(String queueName, T data) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        return boundedBlockingQueue.offer(data);
    }

    /**
     * 订阅指定名称的阻塞队列中的元素变化事件。
     *
     * @param queueName 队列名称
     * @param consumer  元素消费处理函数
     * @param <T>       数据类型泛型
     */
    public static <T> void subscribeBlockingQueue(String queueName, Consumer<T> consumer) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        queue.subscribeOnElements(consumer);
    }
}
