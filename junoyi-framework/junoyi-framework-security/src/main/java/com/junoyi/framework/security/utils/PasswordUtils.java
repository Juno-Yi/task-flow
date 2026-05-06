package com.junoyi.framework.security.utils;

import com.junoyi.framework.core.utils.StringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用 PBKDF2 算法进行密码加密，支持自定义盐值
 *
 * @author Fan
 */
public class PasswordUtils {

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     * 迭代次数（越高越安全，但越慢）
     */
    private static final int ITERATIONS = 10000;

    /**
     * 密钥长度
     */
    private static final int KEY_LENGTH = 256;

    /**
     * 盐值长度
     */
    private static final int SALT_LENGTH = 16;

    private PasswordUtils() {}

    /**
     * 加密密码（使用指定盐值）
     *
     * @param rawPassword 明文密码
     * @param salt        盐值
     * @return 加密后的密码（Base64 编码）
     */
    public static String encrypt(String rawPassword, String salt) {
        if (StringUtils.isBlank(rawPassword))
            throw new IllegalArgumentException("密码不能为空");
        if (StringUtils.isBlank(salt))
            throw new IllegalArgumentException("盐值不能为空");

        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 加密密码（自动生成盐值）
     *
     * @param rawPassword 明文密码
     * @return EncryptResult 包含加密后的密码和盐值
     */
    public static EncryptResult encrypt(String rawPassword) {
        String salt = generateSalt();
        String encodedPassword = encrypt(rawPassword, salt);
        return new EncryptResult(encodedPassword, salt);
    }

    /**
     * 验证密码是否匹配
     *
     * @param rawPassword     明文密码
     * @param salt            盐值
     * @param encodedPassword 加密后的密码
     * @return true=匹配，false=不匹配
     */
    public static boolean matches(String rawPassword, String salt, String encodedPassword) {
        if (StringUtils.isBlank(rawPassword) || StringUtils.isBlank(salt) || StringUtils.isBlank(encodedPassword))
            return false;

        try {
            String newEncodedPassword = encrypt(rawPassword, salt);
            return encodedPassword.equals(newEncodedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机盐值
     *
     * @return Base64 编码的盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密结果类
     */
    public static class EncryptResult {
        private final String encodedPassword;
        private final String salt;

        public EncryptResult(String encodedPassword, String salt) {
            this.encodedPassword = encodedPassword;
            this.salt = salt;
        }

        /**
         * 获取加密后的密码
         */
        public String getEncodedPassword() {
            return encodedPassword;
        }

        /**
         * 获取盐值
         */
        public String getSalt() {
            return salt;
        }
    }
}
