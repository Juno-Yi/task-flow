package com.junoyi.system.enums;

/**
 * 系统用户状态枚举类
 *
 * @author Fan
 */
public enum SysUserStatus {
    /**
     * 正常状态
     */
    NORMAL(1, "正常"),

    /**
     * 禁用状态
     */
    DISABLED(0, "禁用"),

    /**
     * 锁定状态（密码错误次数过多）
     */
    LOCKED(2, "锁定");

    private final int code;
    private final String label;

    SysUserStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static SysUserStatus fromCode(int code) {
        for (SysUserStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return NORMAL;
    }
}
