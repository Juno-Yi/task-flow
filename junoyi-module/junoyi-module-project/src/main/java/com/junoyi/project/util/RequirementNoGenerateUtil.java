package com.junoyi.project.util;

import com.junoyi.framework.redis.utils.RedisUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 需求编号生成器工具类（基于 Redis 实现）
 * 生成6-12位的随机字符串组合
 * 支持分布式环境，保证唯一性
 *
 * @author Fan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequirementNoGenerateUtil {

    /**
     * Redis 键前缀（用于存储已生成的编号，防止重复）
     */
    private static final String REDIS_KEY_PREFIX = "requirement:no:";

    /**
     * 编号长度（默认8位）
     */
    private static final int CODE_LENGTH = 8;

    /**
     * 字符集：大写字母 + 数字（去除容易混淆的字符：0、O、I、1、L）
     */
    private static final String CHARACTERS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

    /**
     * 安全随机数生成器
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Redis 键过期时间（30天）
     */
    private static final long EXPIRE_TIME = 30L;
    private static final TimeUnit EXPIRE_TIME_UNIT = TimeUnit.DAYS;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 10;

    /**
     * 生成需求编码的方法。
     * 生成6-12位的随机字符串（默认8位），格式为：大写字母+数字组合
     * 例如：REQ-A3K7M2NP、REQ-B9X4Q6WR
     *
     * 使用 Redis 存储已生成的编号，保证分布式环境下的唯一性。
     *
     * @return 返回生成的需求编码字符串。
     */
    public static String generateRequirementCode() {
        RedissonClient client = RedisUtils.getClient();
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            // 生成随机编号
            String code = generateRandomCode();
            String fullCode = "REQ-" + code;
            String redisKey = REDIS_KEY_PREFIX + code;

            // 尝试在 Redis 中设置该编号（如果不存在才设置成功）
            RBucket<String> bucket = client.getBucket(redisKey);
            boolean success = bucket.trySet("1", EXPIRE_TIME, EXPIRE_TIME_UNIT);

            if (success) {
                // 设置成功，说明编号唯一
                return fullCode;
            }

            // 编号已存在，重试
            retryCount++;
        }

        // 达到最大重试次数，抛出异常
        throw new RuntimeException("生成需求编号失败：无法生成唯一编号，请稍后重试");
    }

    /**
     * 生成随机字符串编号
     *
     * @return 返回随机字符串（默认8位）
     */
    private static String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    /**
     * 生成指定长度的随机字符串编号
     *
     * @param length 编号长度（6-12位）
     * @return 返回随机字符串
     */
    public static String generateRequirementCode(int length) {
        if (length < 6 || length > 12) {
            throw new IllegalArgumentException("编号长度必须在6-12位之间");
        }

        RedissonClient client = RedisUtils.getClient();
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            // 生成随机编号
            String code = generateRandomCode(length);
            String fullCode = "REQ-" + code;
            String redisKey = REDIS_KEY_PREFIX + code;

            // 尝试在 Redis 中设置该编号（如果不存在才设置成功）
            RBucket<String> bucket = client.getBucket(redisKey);
            boolean success = bucket.trySet("1", EXPIRE_TIME, EXPIRE_TIME_UNIT);

            if (success) {
                // 设置成功，说明编号唯一
                return fullCode;
            }

            // 编号已存在，重试
            retryCount++;
        }

        // 达到最大重试次数，抛出异常
        throw new RuntimeException("生成需求编号失败：无法生成唯一编号，请稍后重试");
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 字符串长度
     * @return 返回随机字符串
     */
    private static String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}

