package com.junoyi.framework.captcha.generator;

import cn.hutool.core.util.IdUtil;
import com.junoyi.framework.captcha.domain.CaptchaResult;
import com.junoyi.framework.captcha.enums.CaptchaType;
import com.junoyi.framework.captcha.properties.CaptchaProperties;
import com.junoyi.framework.captcha.store.CaptchaStore;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * 图片验证码生成器 - 支持字体扭曲和多种干扰
 *
 * @author Fan
 */
public class ImageCaptchaGenerator implements CaptchaGenerator {

    private final CaptchaProperties properties;
    private final CaptchaStore captchaStore;
    private final Random random = new Random();

    // 排除易混淆字符
    private static final String CHAR_SET = "2345678abcdefghjkmnpqrstuvwxyz";
    // 字体颜色池
    private static final Color[] COLORS = {
            new Color(0, 100, 180), new Color(180, 60, 60), new Color(60, 140, 60),
            new Color(140, 80, 160), new Color(180, 120, 40), new Color(80, 80, 80)
    };
    // 字体池
    private static final String[] FONTS = {"Arial", "Georgia", "Verdana", "Tahoma", "Times New Roman"};

    public ImageCaptchaGenerator(CaptchaProperties properties, CaptchaStore captchaStore) {
        this.properties = properties;
        this.captchaStore = captchaStore;
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.IMAGE;
    }

    @Override
    public CaptchaResult generate() {
        CaptchaProperties.ImageCaptcha config = properties.getImage();
        String captchaId = IdUtil.fastSimpleUUID();
        String code;

        if ("math".equalsIgnoreCase(config.getCodeType())) {
            // 自定义数学表达式，保证结果在0-100之间且不为负数
            String[] mathResult = generateMathExpression();
            code = mathResult[0];
            String answer = mathResult[1];
            captchaStore.save(captchaId, answer, properties.getExpireSeconds());
        } else {
            code = generateRandomCode(config.getLength());
            captchaStore.save(captchaId, code.toLowerCase(), properties.getExpireSeconds());
        }

        String imageBase64 = generateImage(code, config);

        return new CaptchaResult()
                .setCaptchaId(captchaId)
                .setType(CaptchaType.IMAGE)
//                .setImage("data:image/png;base64," + imageBase64)
                .setImage(imageBase64)
                .setExpireSeconds(properties.getExpireSeconds());
    }

    @Override
    public boolean validate(String captchaId, Object params) {
        if (params == null) return false;
        String inputCode = params.toString().toLowerCase();
        return captchaStore.validateAndRemove(captchaId, inputCode);
    }

    /**
     * 生成随机验证码
     */
    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片
     */
    private String generateImage(String code, CaptchaProperties.ImageCaptcha config) {
        int width = config.getWidth();
        int height = config.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景
        g2d.setColor(new Color(245, 245, 245));
        g2d.fillRect(0, 0, width, height);

        // 绘制干扰线
        drawLines(g2d, width, height, config.getLineCount());

        // 绘制干扰圆圈
        drawCircles(g2d, width, height, config.getCircleCount());

        // 绘制噪点
        drawNoise(g2d, width, height, config.getNoiseCount());

        // 绘制扭曲的文字
        drawDistortedText(g2d, code, width, height, config);

        g2d.dispose();

        // 应用扭曲变换
        if (config.getDistortion() > 0) {
            image = applyDistortion(image, config.getDistortion());
        }

        // 转Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }

    /**
     * 绘制干扰线
     */
    private void drawLines(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(getRandomLightColor());
            g2d.setStroke(new BasicStroke(1 + random.nextFloat() * 2));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制干扰圆圈
     */
    private void drawCircles(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(getRandomLightColor());
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int r = 5 + random.nextInt(15);
            g2d.drawOval(x, y, r, r);
        }
    }

    /**
     * 绘制噪点
     */
    private void drawNoise(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(getRandomLightColor());
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g2d.fillRect(x, y, 2, 2);
        }
    }

    /**
     * 绘制扭曲的文字
     */
    private void drawDistortedText(Graphics2D g2d, String code, int width, int height, CaptchaProperties.ImageCaptcha config) {
        int fontSize = config.getFontSize();
        int charWidth = (width - 20) / code.length();
        int startX = 10;

        for (int i = 0; i < code.length(); i++) {
            String fontName = FONTS[random.nextInt(FONTS.length)];
            int style = random.nextBoolean() ? Font.BOLD : (Font.BOLD | Font.ITALIC);
            Font font = new Font(fontName, style, fontSize + random.nextInt(6) - 3);
            g2d.setFont(font);
            g2d.setColor(COLORS[random.nextInt(COLORS.length)]);

            // 随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.5; // -0.25 到 0.25 弧度
            int x = startX + i * charWidth;
            int y = height / 2 + fontSize / 3 + random.nextInt(10) - 5;

            AffineTransform transform = g2d.getTransform();
            g2d.rotate(angle, x + charWidth / 2.0, y);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            g2d.setTransform(transform);
        }
    }

    /**
     * 应用正弦波扭曲
     */
    private BufferedImage applyDistortion(BufferedImage src, double distortion) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        double amplitude = height * distortion * 0.1;
        double period = width / (2.0 + random.nextDouble() * 2);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 水平方向正弦波扭曲
                double offsetX = amplitude * Math.sin(2 * Math.PI * y / period);
                // 垂直方向正弦波扭曲
                double offsetY = amplitude * 0.5 * Math.sin(2 * Math.PI * x / period);

                int srcX = (int) (x + offsetX);
                int srcY = (int) (y + offsetY);

                if (srcX >= 0 && srcX < width && srcY >= 0 && srcY < height) {
                    dst.setRGB(x, y, src.getRGB(srcX, srcY));
                } else {
                    dst.setRGB(x, y, new Color(245, 245, 245).getRGB());
                }
            }
        }
        return dst;
    }

    /**
     * 生成数学表达式
     * 规则：两个运算数都是个位数(1-9)，结果在0-99之间
     * @return [表达式, 答案]
     */
    private String[] generateMathExpression() {
        int type = random.nextInt(3); // 0=加法, 1=减法, 2=乘法
        int a, b, result;
        String expression;

        switch (type) {
            case 0: // 加法: 两个个位数相加，结果最大18
                a = random.nextInt(9) + 1;  // 1-9
                b = random.nextInt(9) + 1;  // 1-9
                result = a + b;
                expression = a + "+" + b + "=?";
                break;
            case 1: // 减法: 保证 a >= b，结果不为负
                a = random.nextInt(9) + 1;  // 1-9
                b = random.nextInt(a) + 1;  // 1 到 a
                result = a - b;
                expression = a + "-" + b + "=?";
                break;
            case 2: // 乘法: 两个个位数相乘，结果最大81
            default:
                a = random.nextInt(9) + 1;  // 1-9
                b = random.nextInt(9) + 1;  // 1-9
                result = a * b;
                expression = a + "×" + b + "=?";
                break;
        }

        return new String[]{expression, String.valueOf(result)};
    }

    /**
     * 获取随机浅色（用于干扰元素）
     */
    private Color getRandomLightColor() {
        return new Color(
                150 + random.nextInt(100),
                150 + random.nextInt(100),
                150 + random.nextInt(100)
        );
    }
}
