package com.junoyi.project.util;

import com.junoyi.framework.redis.utils.RedisUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;

/**
 * 项目编号生成器工具类（基于 Redis 实现）
 * 支持分布式环境，每天自动重置序列号
 * 序列号为4位随机数字，避免规律性
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectNoGenerateUtil {

    /**
     * Redis 键前缀
     */
    private static final String REDIS_KEY_PREFIX = "project:no:";

    /**
     * 序列号起始范围（4位数字：1000-9999）
     */
    private static final int SEQUENCE_START_MIN = 1000;
    private static final int SEQUENCE_START_MAX = 9999;

    /**
     * 随机数生成器
     */
    private static final Random RANDOM = new Random();

    /**
     * 生成项目编码的方法。
     * 该方法结合当前日期和一个递增序列号，生成唯一的项目编码。
     * 格式为：PJ-YYYYMMDD-XXXX，其中YYYYMMDD为当前日期，XXXX为四位数的序列号。
     * 每天的序列号从随机值（1000-9999之间）开始，避免规律性。
     *
     * 使用 Redis 原子递增保证分布式环境下的唯一性和线程安全。
     *
     * @return 返回生成的项目编码字符串。
     */
    public static String generateProjectCode() {
        String currentDate = getDate();
        String redisKey = REDIS_KEY_PREFIX + currentDate;

        // 检查是否是新的一天（键不存在）
        if (!RedisUtils.hasKey(redisKey)) {
            // 初始化为随机起始值（1000-9999）
            int randomStart = RANDOM.nextInt(SEQUENCE_START_MAX - SEQUENCE_START_MIN + 1) + SEQUENCE_START_MIN;
            RedisUtils.setCacheObject(redisKey, randomStart);
            setExpireAtEndOfDay(redisKey);
        }

        // 使用 Redis 原子递增获取序列号
        long sequence = RedisUtils.incrAtomicValue(redisKey);

        return "PJ-" + currentDate + "-" + formatSequence(sequence);
    }

    /**
     * 获取当前日期的字符串表示。
     * 使用SimpleDateFormat将当前日期格式化为"yyyyMMdd"格式。
     *
     * @return 返回格式化后的日期字符串。
     */
    private static String getDate() {
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 格式化序列号为四位数字符串。
     * 如果序列号超过9999，则自动扩展位数（10000、10001...）
     *
     * @param sequence 序列号
     * @return 返回格式化后的序列号字符串（至少四位）
     */
    private static String formatSequence(long sequence) {
        // 如果序列号小于10000，格式化为四位数；否则保持原样
        if (sequence < 10000) {
            return String.format("%04d", sequence);
        }
        return String.valueOf(sequence);
    }

    /**
     * 设置 Redis 键在当天结束时过期。
     * 这样可以确保第二天自动重置序列号。
     *
     * @param redisKey Redis 键名
     */
    private static void setExpireAtEndOfDay(String redisKey) {
        // 计算到当天结束还有多少秒
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long secondsUntilEndOfDay = Duration.between(now, endOfDay).getSeconds() + 1;

        // 设置过期时间
        RedisUtils.expire(redisKey, secondsUntilEndOfDay);
    }
}
